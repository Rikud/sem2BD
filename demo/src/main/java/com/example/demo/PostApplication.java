package com.example.demo;

import com.dturan.api.PostApi;
import com.dturan.model.Post;
import com.dturan.model.PostFull;
import com.dturan.model.PostUpdate;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/post/")
public class PostApplication implements PostApi {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String SEARCH_POST_BY_ID= "SELECT * FROM posts WHERE id = ?";

    @Override
    @ApiOperation(value = "Получение информации о ветке обсуждения", notes = "Получение информации о ветке обсуждения по его имени. ", response = PostFull.class, tags={  })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Информация о ветке обсуждения. ", response = PostFull.class),
            @ApiResponse(code = 404, message = "Ветка обсуждения отсутсвует в форуме. ", response = PostFull.class) })
    @RequestMapping(value = "/{id}/details",
            produces = { "application/json" },
            method = RequestMethod.GET)
    public ResponseEntity<PostFull> postGetOne(
    @ApiParam(value = "Идентификатор сообщения.",required=true ) @PathVariable("id") BigDecimal id,
    @ApiParam(value = "Включение полной информации о соответвующем объекте сообщения. Если тип объекта" +
    " не указан, то полная информация об этих объектах не передаётся. ",
    allowableValues = "USER, FORUM, THREAD")
    @RequestParam(value = "related", required = false) List<String> related) {
        Post post = null;
        try {
            post = jdbcTemplate.queryForObject(SEARCH_POST_BY_ID, new Object[] { id }, new PostsMapper());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        PostFull postFull = new PostFull();
        return new ResponseEntity<>(postFull, HttpStatus.CREATED);
    }

    @Override
    @ApiOperation(value = "Изменение сообщения", notes = "Изменение сообщения на форуме. Если сообщение поменяло текст, то оно должно получить отметку `isEdited`. ", response = Post.class, tags={  })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Информация о сообщении. ", response = Post.class),
            @ApiResponse(code = 404, message = "Сообщение отсутсвует в форуме. ", response = Post.class) })
    @RequestMapping(value = "/{id}/details",
            produces = { "application/json" },
            consumes = { "application/json" },
            method = RequestMethod.POST)
    public ResponseEntity<Post> postUpdate(@ApiParam(value = "Идентификатор сообщения.",required=true ) @PathVariable("id") BigDecimal id,
    @ApiParam(value = "Изменения сообщения." ,required=true ) @RequestBody PostUpdate post) {
        Post post1 = new Post();
        return new ResponseEntity<>(post1, HttpStatus.CREATED);
    }
}