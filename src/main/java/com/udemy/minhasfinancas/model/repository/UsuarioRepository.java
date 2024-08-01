package com.udemy.minhasfinancas.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.udemy.minhasfinancas.model.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	//pelo fato de usar o findByEmail essa nomenclatura o Spring já entende que deve pesquisar por Email, já que existe esse atributo na classe e na tabela.
	//Caso fossem 2 valores, ficaria findByEmailAndNome, daí como parãmetro eu passo dois valores no método, String email, String nome. O nome do atributo da assinatura, o nome da propriedade
	//tem que ser o mesmo nome da classe. O nome disso é Query Methods
	//Optional<Usuario> findByEmail(String email);
	
	//Também pode ser feito dessa forma, que é o mesmo esquema de cima.
	boolean existsByEmail(String email);	
	
	Optional<Usuario> findByEmail(String email);//Esse Optional ele sempre vai retornar a classe, podendo ser até vazia. Isso evita de tratar erro de NullPointException caso o retorno seja Null
}
