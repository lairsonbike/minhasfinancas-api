package com.udemy.minhasfinancas.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import com.udemy.minhasfinancas.model.enums.StatusLancamento;
import com.udemy.minhasfinancas.model.enums.TipoLancamento;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "lancamento", schema = "financas")
@Builder
@Data
public class Lancamento {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "descricao")
	private String descricao;
	
	@Column(name = "mes")
	private Integer mes;

	@Column(name = "ano")
	private Integer ano;
	
	@ManyToOne//Muitos lançamentos para 1 usuário
	@JoinColumn(name = "id_usuario")//Indica que a coluna faz parte de um relacionamento
	private Usuario usuario;
	
	@Column(name = "valor")
	private BigDecimal valor;
	
	@Column(name = "data_cadastro")
	@Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)//Por enquanto o Postgres não converte datas do tipo LocalDate, nesse caso precisa usar um conversor, é só add essa anotação.
	private LocalDate dataCadastro;
	
	@Column(name = "tipo")
	@Enumerated(value = EnumType.STRING) //Aceita somente as descrições que estão na classe TipoLancamento e quando salvar vai salvar o texto no banco
	private TipoLancamento tipo;
	
	@Column(name = "status")
	@Enumerated(value = EnumType.STRING) //Aceita somente as descrições que estão na classe TipoLancamento e quando salvar vai salvar o texto no banco
	private StatusLancamento status;

	
	
	
	
	
	
}



















