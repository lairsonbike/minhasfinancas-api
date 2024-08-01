package com.udemy.minhasfinancas.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table( name = "usuario", schema = "financas")
@Builder // Doideira. É criado vários métodos dentro da classe usuário que os nomes desses métodos são os mesmos dos atributos. Ou seja, o atributo Nome vai ter um método nome. No final vai ter um
//método build que retornará um objeto do tipo Usuario.
@Data//Métodos @Getter @Setter @RequiredArgsConstructor @ToString @EqualsAndHashCode juntos
@NoArgsConstructor//vai criar um construtor com tudo vazio
@AllArgsConstructor//vai criar um construtor com todos os argumentos
public class Usuario {
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "nome")
	private String nome;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "senha")
	private String senha;

	
}
