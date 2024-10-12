package com.udemy.minhasfinancas.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.udemy.minhasfinancas.exception.ErroAutenticacao;
import com.udemy.minhasfinancas.exception.RegraNegocioException;
import com.udemy.minhasfinancas.model.entity.Usuario;
import com.udemy.minhasfinancas.model.repository.UsuarioRepository;
import com.udemy.minhasfinancas.service.UsuarioService;

@Service //Diz para o Spring gerenciar essa classe ao criar uma instancia para injeção de dependência
public class UsuarioServiceImpl implements UsuarioService {

	
	private UsuarioRepository repository;
	
	@Autowired //Instancia. 
	public UsuarioServiceImpl(UsuarioRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		
		Optional<Usuario> usuario = repository.findByEmail(email);
		
		if(!usuario.isPresent()) {//Esse isPresent é para verificar os dados estão presentes, já que no método foi utilizado o Optional, evitanto tratar o NullPointException
			throw new ErroAutenticacao("Usuário não encontrado para o email informado");
		}
		if(!usuario.get().getSenha().equals(senha)) {//Esse método .get() retorna a classe que está dentro do Optional
			throw new ErroAutenticacao("Senha inválida");
		}
		return usuario.get();
	}

	@Override
	@Transactional//Cria uma transação salva e commita
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		return repository.save(usuario);
	}

	@Override
	public void validarEmail(String email) {
		boolean existe = repository.existsByEmail(email);
		if(existe) {
			throw new RegraNegocioException("Já existe um usuário cadastrado com este email.");
		}
	}

	@Override
	public Optional<Usuario> obterPorId(Long id) {
		return repository.findById(id);
	}

}
