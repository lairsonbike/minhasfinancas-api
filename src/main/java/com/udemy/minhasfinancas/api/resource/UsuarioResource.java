package com.udemy.minhasfinancas.api.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.udemy.minhasfinancas.api.dto.UsuarioDTO;
import com.udemy.minhasfinancas.exception.ErroAutenticacao;
import com.udemy.minhasfinancas.exception.RegraNegocioException;
import com.udemy.minhasfinancas.model.entity.Usuario;
import com.udemy.minhasfinancas.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")//essa já fica sendo a raiz de toda requisição que tiver localhost/api/usuarios
public class UsuarioResource {

	@GetMapping
	public String helloWordl() {
		return "hello world!!!";
	}
	
	private UsuarioService service;
	
	public UsuarioResource(UsuarioService service) {
		this.service = service;
	}
	
	@PostMapping("/autenticar")//Não pode existir o mesmo mapeamento, talvez a não ser que a assinatura seja diferente.
	public ResponseEntity autenticar( @RequestBody UsuarioDTO dto) {
		
		try {
			Usuario usuarioAutentitcado = service.autenticar(dto.getEmail(), dto.getSenha());
			return ResponseEntity.ok(usuarioAutentitcado);//O ok retorna o código 200

		} catch (ErroAutenticacao e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
	}
	
	@PostMapping
	public ResponseEntity salvar(@RequestBody UsuarioDTO dto) {
		
		Usuario usuario = Usuario.builder().nome(dto.getNome()).email(dto.getEmail()).senha(dto.getSenha()).build();
		
		try {
			Usuario usuarioSalvo = service.salvarUsuario(usuario);
			return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);//O CREATED retorna o código 201
		}catch(RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	
}
