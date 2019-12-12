package com.evilcorp.demo;

import com.evilcorp.demo.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.PostConstruct;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class TestcontainersSpringDemoApplicationTests {
	@Autowired
	UserRepository userRepository;
	private User createdUser;

	@PostConstruct
	void setUp() {
		createdUser = new User();
		createdUser.setName("Fry");
		userRepository.save(createdUser);
	}

	@Test
	void userRepositoryLoaded() {
		assertNotNull(userRepository);
	}

	@Test
	void userAdded() {
		final Optional<User> loadedUser = userRepository.findById(createdUser.getUserId());
		assertTrue(loadedUser.isPresent());
		assertEquals("Fry", loadedUser.get().getName());
		assertNotSame(createdUser, loadedUser.get());
	}
}
