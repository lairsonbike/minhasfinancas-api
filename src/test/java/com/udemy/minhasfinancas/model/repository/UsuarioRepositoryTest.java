package com.udemy.minhasfinancas.model.repository;

//import org.junit.Test;
//import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
//import org.springframework.test.context.junit4.SpringRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.udemy.minhasfinancas.model.entity.Usuario;

@SpringBootTest
//@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")//Ele vai procurar outra conexão do banco virtual de memória para teste. No caso o application-test.properties
public class UsuarioRepositoryTest {

	@Autowired
	UsuarioRepository repository;
	
	//@Autowired
	//TestEntityManager entityManager;
	
	@Test
	public void deveVerificarAExistenciaDeUmEmail() {
		
		//Cenário
		Usuario usuario = criarUsuario();
		
		System.out.println(usuario.toString());
		
		repository.save(usuario);
		
		//Ação / Execução
		boolean result = repository.existsByEmail(usuario.getEmail());
		
		//Verificação
		org.assertj.core.api.Assertions.assertThat(result).isTrue();
	}
	
	@Test
	public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComOEmail() {
		
		//cenário
		repository.deleteAll();
		
		//ação
		boolean result = repository.existsByEmail("lairson@email.com2");
		
		//Verificação
		org.assertj.core.api.Assertions.assertThat(result).isFalse();
		
	}
	
	@Test
	public void devePersistirUmUsuarioNaBaseDeDados() {
		//Cenário
		Usuario usuario = criarUsuario();
		
		//Ação
		Usuario usuarioSalvo = repository.save(usuario);
		
		//Verificação
		org.assertj.core.api.Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
		
	}
	
	@Test
	public void deveBuscarUmUsuarioPorEmail() {
		//cenario
		repository.deleteAll();
		
		Usuario usuario = criarUsuario();
		//entityManager.persist(usuario);
		repository.save(usuario);
		
		//Verificação
		Optional<Usuario> result = repository.findByEmail("lairson@gmail.com");
		
		//Resultado
		org.assertj.core.api.Assertions.assertThat(result.isPresent()).isTrue();
	}
	
	
	@Test
	public void deveRetornarVazioAoBuscarUsuarioPorEmailQuandoNaoExistirNaBase() {
		//cenario
		repository.deleteAll();
		
		//Verificação
		Optional<Usuario> result = repository.findByEmail("lairson@gmail.com");
		
		//Resultado
		org.assertj.core.api.Assertions.assertThat(result.isPresent()).isFalse();
	}
	
	
	
	public static Usuario criarUsuario() {
		return Usuario.builder()
				.nome("lairson")
				.email("lairson@gmail.com")
				.senha("123")
				.build();
	}
}


















