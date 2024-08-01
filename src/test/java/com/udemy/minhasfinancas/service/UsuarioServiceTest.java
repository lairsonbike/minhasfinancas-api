package com.udemy.minhasfinancas.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.udemy.minhasfinancas.exception.ErroAutenticacao;
import com.udemy.minhasfinancas.exception.RegraNegocioException;
import com.udemy.minhasfinancas.model.entity.Usuario;
import com.udemy.minhasfinancas.model.repository.UsuarioRepository;
import com.udemy.minhasfinancas.service.impl.UsuarioServiceImpl;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
//@DataJdbcTest//Cria uma instancia do banco de dados dutante o teste e depois apaga
//@AutoConfigureTestDatabase(replace = Replace.NONE)//Para não sobrescrever as configurações realizadas no @ActiveProfiles(test)
//@ContextConfiguration(classes = {UsuarioServiceImpl.class, UsuarioServiceTest.class})
public class UsuarioServiceTest {

	//@Autowired
	//UsuarioService service;
	@SpyBean//Faz o mesmo que o Mokito mas usa as classes originais e não uma fake.
	UsuarioServiceImpl service;
	
	@MockBean//Vai injetar uma instancia fake para teste, nunca chamando o método original.
	UsuarioRepository repository;
	
	/*
	@Before
	public void setUp() {
		//repository = Mockito.mock(UsuarioRepository.class);
		service = Mockito.spy(UsuarioServiceImpl.class);
		//service = new UsuarioServiceImpl(repository);
	}
	*/
	@Test
	public void deveSalvarUmUsuario() {
		assertDoesNotThrow(() -> { //Espero que não lance nenhuma exceção. Caso seja lançado alguma exceção, vai dar erro no teste.
			//Cenário
			Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
			Usuario usuario = Usuario.builder().id(1l).nome("nome").email("email@email.com").senha("senha").build();
			Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
			
			//Ação
			Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
			
			//Verificação
			Assertions.assertThat(usuarioSalvo).isNotNull();
			Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1l);
			Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
			Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
			Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
		});
	}
	
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		
		assertThrows(RegraNegocioException.class, ()->{ //Espero que seja lançado a exceção RegraNegocioException. Caso não seja lançado essa exceção, vai dar erro no teste.
	
			//Cenário
			String email = "email@email.com";
			Usuario usuario = Usuario.builder().email(email).build();
			Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
			
			//acão
			service.salvarUsuario(usuario);
		
			//Verificação
			Mockito.verify(repository, Mockito.never()).save(usuario);//Espero que ele nunca tenha chamado o método save
			
		});
	}
	
	@Test
	public void deveAtutenciarUmUsuarioComSucesso() {
		assertDoesNotThrow(() -> { //Espero que não lance nenhuma exceção. Caso seja lançado alguma exceção, vai dar erro no teste.
				//Cenário
				String email = "lairson@gmail.com";
				String senha = "senha123";
				
				Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
				
				Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
				
				//Acao
				Usuario result = service.autenticar(email, senha);
				
				//Verificação
				Assertions.assertThat(result).isNotNull();
		});
	}
	
	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
		
		//assertThrows(ErroAutenticacao.class, ()->{ //Espero que seja lançado a exceção ErroAutenticacao. Caso não seja lançado essa exceção, vai dar erro no teste.
			
			//Cenário
			Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
			
			//Ação
			
			Throwable exception = Assertions.catchThrowable( () -> service.autenticar("lairson@gmail.com", "123") );
			Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Usuário não encontrado para o email informado");//Nesse caso a mensagem de erro lançada deve ser a mesma mensagem que está dentro desse hasMessage.
			
			
			//Verificação
			
		//});
	}
	
	
	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {
		
		//assertThrows(ErroAutenticacao.class, ()->{ //Espero que seja lançado a exceção ErroAutenticacao. Caso não seja lançado essa exceção, vai dar erro no teste.
			
			//Cenário
			String senha = "senha";
			Usuario usuario = Usuario.builder().email("lairson@gmail.com").senha(senha).build();
			
			Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
			
			//Ação
			Throwable exception = Assertions.catchThrowable( () -> service.autenticar("lairson@gmail.com", "senhaErrada") );
			Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha inválida");//Nesse caso a mensagem de erro lançada deve ser a mesma mensagem que está dentro desse hasMessage.
			
			//Verificação
			
		//});
	}
	
	@Test
	public void deveValidarEmail() {
		assertDoesNotThrow(() -> { //Espero que não lance nenhuma exceção. Caso seja lançado alguma exceção, vai dar erro no teste.
			
			//cenário
			Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);//Simula um retorno falso do método existsByEmail através do Mockito
			//repository.deleteAll();
			
			//ação
			service.validarEmail("lairson@email.com");
			
		});
	}
	
	@Test
	public void deveLancarErrorAoValidarEmailQuandoExistirEmailCadastrado() {
		
		assertThrows(RegraNegocioException.class, ()->{ //Espero que seja lançado a exceção RegraNegocioException. Caso não seja lançado essa exceção, vai dar erro no teste.
			
			//Cenário
			Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);//Simula um retorno falso do método existsByEmail através do Mockito
			//Usuario usuario = Usuario.builder().nome("usuario").email("email@email.com").build();
			//repository.save(usuario);
			
			//ação
			service.validarEmail("email@email.com");
			
		});
		
		
	}
}











