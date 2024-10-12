package com.udemy.minhasfinancas.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.udemy.minhasfinancas.exception.RegraNegocioException;
import com.udemy.minhasfinancas.model.entity.Lancamento;
import com.udemy.minhasfinancas.model.enums.StatusLancamento;
import com.udemy.minhasfinancas.model.enums.TipoLancamento;
import com.udemy.minhasfinancas.model.repository.LancamentoRepository;
import com.udemy.minhasfinancas.service.LancamentoService;

@Service
public class LancamentoServiceImpl implements LancamentoService {

	LancamentoRepository repository;
	
	public LancamentoServiceImpl(LancamentoRepository repository) {
		this.repository = repository;
	}
	
	@Override
	@Transactional
	public Lancamento salvar(Lancamento lancamento) {
		validar(lancamento);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		return repository.save(lancamento);
	}

	@Override
	@Transactional
	public Lancamento atualizar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());//Caso o lançamento não tenha um ID, ou seja, não esteja cadastrado para atualizar, ele retorna erro.
		validar(lancamento);
		return repository.save(lancamento);
	}

	@Override
	@Transactional
	public void deletar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());//Caso o lançamento não tenha um ID, ou seja, não esteja cadastrado para ser deletado, ele retorna erro.
		repository.delete(lancamento);
	}

	@Override
	@Transactional(readOnly = true)//Transação somente leitura. Ao colocar readOnly true, não ocorre de ter Insert, Delet ou Update. Vai ocorrer somente um Select. Isso garante que os dados do banco não serão alterados. É uma boa prática declarar isso nas funçoes somente de consulta.
	public List<Lancamento> buscar(Lancamento lancamentoFiltro) {
		
		// Já faz a pesquisa somente com os dados que foram preenchidos.
		//O StraingMatcher.CONTAINING quando houver string, fazer a função do Like, ou seja, pesquisar dentro da coluna que contenha a palavra
		//withIgnoreCase para ignorar letrar maiúsculas
		//ExampleMachter.matching() serve para colocar parâmetros na pesquisa, como citados acime. Essa parte depois da vírgula é opcional, pode-se passar somente o objeto lancamentoFiltro.
		Example example = Example.of(lancamentoFiltro, ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));
		
		return repository.findAll(example);
	}

	@Override
	public void atualizarStatus(Lancamento lancamento, StatusLancamento status) {
		lancamento.setStatus(status);
		atualizar(lancamento);
	}

	@Override
	public void validar(Lancamento lancamento) {
		
		if(lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals("")) {
			throw new RegraNegocioException("Informe uma Descrição válida.");
		}
		
		if(lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12) {
			throw new RegraNegocioException("Informe um Mês válido.");
		}
		
		if(lancamento.getAno() ==  null || lancamento.getAno().toString().length() != 4) {
			throw new RegraNegocioException("Informe um Ano válido.");
		}
		
		if(lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null) {
			throw new RegraNegocioException("Informe um Usuário.");
		}
		
		if(lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1 ) {
			throw new RegraNegocioException("Informe um Valor válido.");
		}
		
		if(lancamento.getTipo() == null) {
			throw new RegraNegocioException("Informe um tipo de Lançamento.");
		}
		
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Lancamento> obterPorId(Long id) {
		return repository.findById(id);
	}

	@Override
	public BigDecimal obterSaldoPorUsuario(Long id) {
		BigDecimal receitas = repository.obterSaldoPorTipoLancamentoEUsuario(id, TipoLancamento.RECEITA);
		BigDecimal despesas = repository.obterSaldoPorTipoLancamentoEUsuario(id, TipoLancamento.DESPESA);
		
		if(receitas == null) {
			receitas = BigDecimal.ZERO;
		}
		if(despesas == null) {
			despesas = BigDecimal.ZERO;
		}
		return receitas.subtract(despesas);
	}

}









