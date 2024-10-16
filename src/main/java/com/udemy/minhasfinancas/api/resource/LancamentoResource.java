package com.udemy.minhasfinancas.api.resource;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

import com.udemy.minhasfinancas.api.dto.AtualizaStatusDTO;
import com.udemy.minhasfinancas.api.dto.LancamentoDTO;
import com.udemy.minhasfinancas.exception.RegraNegocioException;
import com.udemy.minhasfinancas.model.entity.Lancamento;
import com.udemy.minhasfinancas.model.entity.Usuario;
import com.udemy.minhasfinancas.model.enums.StatusLancamento;
import com.udemy.minhasfinancas.model.enums.TipoLancamento;
import com.udemy.minhasfinancas.service.LancamentoService;
import com.udemy.minhasfinancas.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor//faz com que seja construido um construtor com todos os argumantos obrigatorios, os que tem FINAL
public class LancamentoResource {

	private final LancamentoService service;
	private final UsuarioService usuarioService;
	
	@GetMapping
	public ResponseEntity buscar(
			//Poderia fazer assim também, mas nesse caso todos os itens seriam opcionais. No outro exemplo eu..
			//...consido dizer quais são obrigatórios ou não.
			//@RequestParam Map<String, String> params
			
			@RequestParam(value = "descricao", required = false) String descricao,
			@RequestParam(value = "mes", required = false) Integer mes,
			@RequestParam(value = "ano", required = false) Integer ano,
			@RequestParam("usuario") Long idUsuario
			) {
		
		Lancamento lancamentoFiltro = new Lancamento();
		lancamentoFiltro.setDescricao(descricao);
		lancamentoFiltro.setMes(mes);
		lancamentoFiltro.setAno(ano);
		
		Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
		if(!usuario.isPresent()) {
			return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Usuário não encontrado para o id informado");
		}else {
			lancamentoFiltro.setUsuario(usuario.get());
		}
		
		List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
		return ResponseEntity.ok(lancamentos);
		
	}
	
	@PostMapping
	public ResponseEntity salvar (@RequestBody LancamentoDTO dto) {
		try {
			Lancamento entidade = converter(dto);
			entidade = service.salvar(entidade);
			//return ResponseEntity.ok(entidade);
			//Posso fazer qualquer um dos dois. 
			return new ResponseEntity(entidade, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PutMapping("{id}") //atualizar. Sempre que for utulizar o Put tem que indicar o id do que eu quero alterar
	public ResponseEntity atualizar( @PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
		
		return service.obterPorId(id).map( entity -> {
			try {
				Lancamento lancamento = converter(dto);
				lancamento.setId(entity.getId());
				service.atualizar(lancamento);
				return ResponseEntity.ok(lancamento);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
			
			
		}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
	}
	
	@PutMapping("{id}/atualiza-status")
	public ResponseEntity atualizarStatus( @PathVariable Long id, @RequestBody AtualizaStatusDTO dto) {
		return service.obterPorId(id).map( entity -> {
			StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
			if(statusSelecionado == null) {
				return ResponseEntity.badRequest().body("Não foi possível atualizar o status do lançamento, envie um status válido");
			}
			
			try {
				entity.setStatus(statusSelecionado);
				service.atualizar(entity);
				return ResponseEntity.ok(entity);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
			
		}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
	}
	
	@DeleteMapping("{id}")// Esse @PathVariable recebe o parâmetro específico vindo da front, que está vinculado a ({"id"}) da anotação do método
	public ResponseEntity deletar( @PathVariable("id") Long id) {
		return service.obterPorId(id).map(entidade -> {
			service.deletar(entidade);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
	}
	
	
	//Converte tudo que veio do front para uma classe usuário
	private Lancamento converter(LancamentoDTO dto) {
		
		Lancamento lancamento = new Lancamento();
		
		lancamento.setId(dto.getId());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());
		
		//orElseThrow caso não encontre um usuário ele já lança uma exceção para a classe de regra de negócio
		Usuario usuario = usuarioService
				.obterPorId(dto.getUsuario())
				.orElseThrow( () -> new RegraNegocioException("Usuário não encontrado para o Id informado") );
		
		lancamento.setUsuario(usuario);
		//Recebe do dto e verifica se essa informação é equivalente ao que tem no TipoLancamento
		if(dto.getTipo() != null) {
			lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
		}
		if(dto.getStatus() != null) {
			lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
		}
		
		return lancamento;
	}
}















