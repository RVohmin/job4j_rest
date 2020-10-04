package ru.job4j.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.repository.PersonRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc

public class PersonControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonRepository personRepository;

    @Test
    public void whenFindAll() throws Exception {
        // создаем объекты
        Person firstPerson = new Person(1, "login", "password");
        Person secondPerson = new Person(2, "login2", "password2");
        // делаем подмену
        given(this.personRepository.findAll()).willReturn(Arrays.asList(firstPerson, secondPerson));
        //получаем сериализованную строку из метода getAll контроллера
        String content = mockMvc.perform(get("/person/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Person> personList = Arrays.asList(firstPerson, secondPerson);
        //объект Jackson, который выполняет сериализацию
        ObjectMapper mapper = new ObjectMapper();
        //записываем в строку массив
        String expect = mapper.writer().writeValueAsString(List.of(firstPerson, secondPerson));
        assertEquals(expect, content);
        assertEquals(personList, personRepository.findAll());
    }

    @Test
    public void whenFindById() throws Exception {
        Person firstPerson = new Person(1, "login", "password");
        given(this.personRepository.findById(firstPerson.getId())).willReturn(java.util.Optional.of(firstPerson));
        String content = mockMvc.perform(get("/person/" + firstPerson.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        String expect = mapper.writer().writeValueAsString(firstPerson);
        Assert.assertEquals(expect, content);
    }

    @Test
    public void whenCreate() throws Exception {

        Person firstPerson = new Person(1, "login", "password");

        ObjectMapper mapper = new ObjectMapper();
        String requestBody = mapper.writer().writeValueAsString(firstPerson);

        given(this.personRepository.save(firstPerson)).willReturn(firstPerson);

        String content = mockMvc.perform(post("/person/")
                .contentType("application/json")
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ArgumentCaptor<Person> argument = ArgumentCaptor.forClass(Person.class);

        verify(personRepository).save(argument.capture());

        Assert.assertEquals(requestBody, content);
        Assert.assertEquals(1, firstPerson.getId());
        Assert.assertEquals("login", firstPerson.getLogin());
        Assert.assertEquals("password", firstPerson.getPassword());
    }

    @Test
    public void whenDelete() throws Exception {
        Person firstPerson = new Person(1, "login", "password");
        Mockito.when(personRepository.findById(firstPerson.getId())).thenReturn(Optional.of(firstPerson));
        mockMvc.perform(delete("/person/" + firstPerson.getId()))
                .andDo(print())
                .andExpect(status().isOk());
    }
}