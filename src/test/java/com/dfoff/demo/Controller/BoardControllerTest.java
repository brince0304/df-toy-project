package com.dfoff.demo.Controller;

import com.dfoff.demo.Domain.Board;
import com.dfoff.demo.Domain.EnumType.BoardType;
import com.dfoff.demo.SecurityConfig.SecurityConfig;
import com.dfoff.demo.Service.BoardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View 컨트롤러 - 인증")
@AutoConfigureMockMvc
@SpringBootTest
@Import(SecurityConfig.class)
class BoardControllerTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MockMvc mvc;
    @Mock
    private final BoardService boardService;

    private final ObjectMapper objectMapper;

    BoardControllerTest(@Autowired MockMvc mvc, @Autowired BoardService boardService,@Autowired ObjectMapper objectMapper) {
        this.mvc = mvc;
        this.boardService = boardService;
        this.objectMapper = objectMapper;
    }

    @Test
    void getBoardListTest() throws Exception {
        //when&then
        mvc.perform(get("/board.df"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));
    }
    @Test
    void getBoardListTestDetail() throws Exception {
        //when&then
        mvc.perform(get("/board.df?boardType=NOTICE&searchType=title&keyword=te"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(model().attributeExists("boardType")).andExpect(model().attributeExists("articles"));
    }

    @Test
    @WithUserDetails ("test")
    void deleteBoardTest() throws Exception {
        //when&then
        mvc.perform(delete("/api/board.df").param("id","1")).andExpect(status().isOk());
    }

    @Test
    void deleteBoardExceptionTest() throws Exception {
        //when&then
        mvc.perform(delete("/api/board.df").param("id","2")).andExpect(status().isUnauthorized());
    }

    @Test
    void deleteBoardNotFoundExceptionTest() throws Exception {
        //when&then
        mvc.perform(delete("/api/board.df").param("id","1")).andExpect(status().isInternalServerError());
    }

    @Test
    @WithUserDetails ("test")
    void updateBoardTest() throws Exception {
        //given
        Board.BoardRequest boardRequest = Board.BoardRequest.builder()
                .id(2L)
                .boardTitle("test")
                .boardContent("test")
                .boardType(BoardType.NOTICE)
                .boardFiles("")
                .build();


        //when&then
        mvc.perform(put("/api/board.df").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(boardRequest)))
                .andExpect(status().isOk());
    }
    @Test
    void updateBoardExceptionTest() throws Exception {
        //given
        Board.BoardRequest boardRequest = Board.BoardRequest.builder()
                .id(2L)
                .boardTitle("test")
                .boardContent("test")
                .boardType(BoardType.NOTICE)
                .boardFiles("")
                .build();
        //when&then
        mvc.perform(put("/api/board.df").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(boardRequest)))
                .andExpect(status().isUnauthorized());
    }


}