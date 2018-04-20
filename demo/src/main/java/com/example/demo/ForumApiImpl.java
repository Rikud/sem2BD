package com.example.demo;

import com.dturan.Mapper.ForumMapper;
import com.dturan.Mapper.ThreadsMapper;
import com.dturan.Mapper.UsersMapper;
import com.dturan.api.ForumApi;
import com.dturan.model.Error;
import com.dturan.model.*;
import com.dturan.model.Thread;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.ArrayList;

@RestController
@RequestMapping("/forum/")
public class ForumApiImpl implements ForumApi {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String CREATE_FORUM_QUERY = "INSERT INTO FORUMS (tittle, slug, user_id) VALUES(?, ?, ?)";
    private static final String SEARCH_USER_BY_NICKNAME_QUERY = "SELECT ID FROM USERS WHERE nickname = ?";
    private static final String SEARCH_FORIM_ID_BY_SLUG  = "SELECT ID FROM forums WHERE slug = ?";
    private static final String SEARCH_FORUM_BY_SLUG = "SELECT * FROM FORUMS WHERE SLUG = ?";
    private static final String SEARCH_USER_NICKNAME_BY_ID_QUERY = "SELECT nickname FROM USERS WHERE id = ?";
    private static final String SEARCH_FORUM_THREADS =
        "SELECT users.nickname as author,\n" +
        "  threads.created,\n" +
        "  forums.slug as forum,\n" +
        "  threads.id,\n" +
        "  threads.message,\n" +
        "  threads.slug,\n" +
        "  threads.tittle,\n" +
        "  threads.votes\n" +
        "FROM forums, threads, users\n" +
        "WHERE forums.slug = ?\n" +
        "      AND forums.id = threads.forum\n" +
        "      AND users.id =  threads.id\n" +
        "ORDER BY threads.created\n" +
                "LIMIT ?";

    private static final String SEARCH_FORUM_THREADS_DESC =
        "SELECT users.nickname as author,\n" +
                "  threads.created,\n" +
                "  forums.slug as forum,\n" +
                "  threads.id,\n" +
                "  threads.message,\n" +
                "  threads.slug,\n" +
                "  threads.tittle,\n" +
                "  threads.votes\n" +
                "FROM forums, threads, users\n" +
                "WHERE forums.slug = ?\n" +
                "      AND forums.id = threads.forum\n" +
                "      AND users.id =  threads.id\n" +
                "ORDER BY threads.created DESC\n" +
                "LIMIT ?";

    private static final String SEARCH_FORUM_THREADS_DATE_LIMIT =
        "SELECT users.nickname as author,\n" +
                "  threads.created,\n" +
                "  forums.slug as forum,\n" +
                "  threads.id,\n" +
                "  threads.message,\n" +
                "  threads.slug,\n" +
                "  threads.tittle,\n" +
                "  threads.votes\n" +
                "FROM forums, threads, users\n" +
                "WHERE forums.slug = ?\n" +
                "      AND forums.id = threads.forum\n" +
                "      AND users.id =  threads.id\n" +
                "      AND threads.created >= ?\n" +
                "ORDER BY threads.created\n" +
                "LIMIT ?";

    private static final String SEARCH_FORUM_THREADS_DATE_LIMIT_DESC =
            "SELECT users.nickname as author,\n" +
                    "  threads.created,\n" +
                    "  forums.slug as forum,\n" +
                    "  threads.id,\n" +
                    "  threads.message,\n" +
                    "  threads.slug,\n" +
                    "  threads.tittle,\n" +
                    "  threads.votes\n" +
                    "FROM forums, threads, users\n" +
                    "WHERE forums.slug = ?\n" +
                    "      AND forums.id = threads.forum\n" +
                    "      AND users.id =  threads.id\n" +
                    "      AND threads.created >= ?\n" +
                    "ORDER BY threads.created DESC\n" +
                    "LIMIT ?";

    private static final String SEARCH_FORUM_USERS =
        "SELECT users.about, users.email, users.fullname, users.nickname\n" +
        "FROM users, posts, forums\n" +
        "WHERE\n" +
        "  forums.slug = ? AND\n" +
        "  posts.forum = forums.id AND\n" +
        "  users.id = posts.author\n" +
        "ORDER BY users.nickname\n" +
        "LIMIT ?";

    private static final String SEARCH_FORUM_USERS_USER_ID_LIMIT =
        "SELECT users.about, users.email, users.fullname, users.nickname\n" +
        "FROM users, posts, forums\n" +
        "WHERE\n" +
        "  forums.slug = ? AND\n" +
        "  posts.forum = forums.id AND\n" +
        "  users.id = posts.author AND\n" +
        "  users.id >= ? \n" +
        "ORDER BY users.nickname\n" +
        "LIMIT ?";

    private static final String SEARCH_FORUM_USERS_DESC =
        "SELECT users.about, users.email, users.fullname, users.nickname\n" +
        "FROM users, posts, forums\n" +
        "WHERE\n" +
        "  forums.slug = ? AND\n" +
        "  posts.forum = forums.id AND\n" +
        "  users.id = posts.author\n" +
        "ORDER BY users.nickname DESC\n" +
        "LIMIT ?";

    private static final String SEARCH_FORUM_USERS_USER_ID_LIMIT_DESC =
        "SELECT users.about, users.email, users.fullname, users.nickname\n" +
        "FROM users, posts, forums\n" +
        "WHERE\n" +
        "  forums.slug = ? AND\n" +
        "  posts.forum = forums.id AND\n" +
        "  users.id = posts.author AND\n" +
        "  users.id >= ? \n" +
        "ORDER BY users.nickname DESC\n" +
        "LIMIT ?";

    private static final String SEARCH_TRHEAD_BY_FORUM_AND_THREAD_SLUGS = "SELECT users.nickname as author,\n" +
            "  threads.created,\n" +
            "  forums.slug as forum,\n" +
            "  threads.id,\n" +
            "  threads.message,\n" +
            "  threads.slug,\n" +
            "  threads.tittle,\n" +
            "  threads.votes\n" +
            "FROM forums, threads, users\n" +
            "WHERE forums.slug = ?\n" +
            "      AND threads.slug = ?\n" +
            "      AND forums.id = threads.forum\n" +
            "      AND users.id =  threads.id;";

    private static final String CREATE_THREAD_QUERY =
            "INSERT INTO THREADS (tittle, author, forum, message, slug, created, votes)\n" +
            "VALUES (?, ?, ?, ?, ?, now(), 0);";

    @Override
    @ApiOperation(value = "Создание форума", notes = "Создание нового форума. ", response = Forum.class, tags={  })
    @ApiResponses(value = {
    @ApiResponse(code = 201, message = "Форум успешно создан. Возвращает данные созданного форума. ", response = Forum.class),
    @ApiResponse(code = 404, message = "Владелец форума не найден. ", response = Forum.class),
    @ApiResponse(code = 409, message = "Форум уже присутсвует в базе данных. Возвращает данные ранее созданного форума. ", response = Forum.class) })
    @RequestMapping(value = "/create",
            produces = { "application/json" },
            consumes = { "application/json" },
            method = RequestMethod.POST)
    public ResponseEntity<?> forumCreate(@ApiParam(value = "Данные форума." ,required=true ) @RequestBody Forum forum) {
        Forum forumSearchResult = null;
        try {
            forumSearchResult = jdbcTemplate.queryForObject(SEARCH_FORUM_BY_SLUG, new Object[] { forum.getSlug() }, new ForumMapper());
            forumSearchResult.setUser(jdbcTemplate.queryForObject(SEARCH_USER_NICKNAME_BY_ID_QUERY, String.class, Long.parseLong(forumSearchResult.getUser())));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (forumSearchResult != null) {
            return new ResponseEntity<>(forumSearchResult, HttpStatus.CONFLICT);
        }
        long user_id = 0;
        try {
            user_id = jdbcTemplate.queryForObject(SEARCH_USER_BY_NICKNAME_QUERY, Long.class, forum.getUser());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new Error("Владелец форума не найден."), HttpStatus.NOT_FOUND);
        }
        int result = 0;
        if (user_id != 0)
            result = jdbcTemplate.update(CREATE_FORUM_QUERY, forum.getTitle(), forum.getSlug(), user_id);
        return new ResponseEntity<>(forum, HttpStatus.CREATED);
    }

    @Override
    @ApiOperation(value = "Получение информации о форуме", notes = "Получение информации о форуме по его идентификаторе. ", response = Forum.class, tags={  })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Информация о форуме. ", response = Forum.class),
            @ApiResponse(code = 404, message = "Форум отсутсвует в системе. ", response = Forum.class) })
    @RequestMapping(value = "/{slug}/details",
            produces = { "application/json" },
            method = RequestMethod.GET)
    public ResponseEntity<?> forumGetOne(@ApiParam(value = "Идентификатор форума.",required=true ) @PathVariable("slug") String slug) {
        Forum forumSearchResult = null;
        try {
            forumSearchResult = jdbcTemplate.queryForObject(SEARCH_FORUM_BY_SLUG, new Object[] { slug }, new ForumMapper());
            forumSearchResult.setUser(jdbcTemplate.queryForObject(SEARCH_USER_NICKNAME_BY_ID_QUERY, String.class, Long.parseLong(forumSearchResult.getUser())));
        }
        catch (Exception e) {
            e.printStackTrace();
            Error error = new Error();
            error.setMessage("Форум отсутсвует в системе.");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(forumSearchResult, HttpStatus.OK);
    }

    @Override
    @ApiOperation(value = "Список ветвей обсужления форума", notes = "Получение списка ветвей обсужления данного форума. Ветви обсуждения выводятся отсортированные по дате создания. ", response = Threads.class, tags={  })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Информация о ветках обсуждения на форуме. ", response = Threads.class),
            @ApiResponse(code = 404, message = "Форум отсутсвует в системе. ", response = Threads.class) })
    @RequestMapping(value = "/{slug}/threads",
            produces = { "application/json" },
            method = RequestMethod.GET)
    public ResponseEntity<?> forumGetThreads(@ApiParam(value = "Идентификатор форума.",required=true ) @PathVariable("slug") String slug,
    @Min(1) @Max(10000) @ApiParam(value = "Максимальное кол-во возвращаемых записей.", defaultValue = "100") @RequestParam(value = "limit", required = false, defaultValue="100") BigDecimal limit,
    @ApiParam(value = "Дата создания ветви обсуждения, с которой будут выводиться записи (ветвь обсуждения с указанной датой попадает в результат выборки). ") @RequestParam(value = "since", required = false) DateTime since,
    @ApiParam(value = "Флаг сортировки по убыванию. ") @RequestParam(value = "desc", required = false) Boolean desc) {

        ArrayList<Thread> threads = null;

        try {
            if (since == null) {
             if (!desc) {
                 threads = (ArrayList<Thread>)jdbcTemplate.query(SEARCH_FORUM_THREADS, new Object[] { slug, limit}, new ThreadsMapper());
             } else {
                 threads = (ArrayList<Thread>)jdbcTemplate.query(SEARCH_FORUM_THREADS_DESC, new Object[] { slug, limit}, new ThreadsMapper());
             }
            } else {
                if (!desc) {
                    threads = (ArrayList<Thread>)jdbcTemplate.query(SEARCH_FORUM_THREADS_DATE_LIMIT, new Object[] { slug, limit, since}, new ThreadsMapper());
                } else {
                    threads = (ArrayList<Thread>)jdbcTemplate.query(SEARCH_FORUM_THREADS_DATE_LIMIT_DESC, new Object[] { slug, limit, since}, new ThreadsMapper());
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Error error = new Error();
            error.setMessage("Форум отсутсвует в системе.");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(threads, HttpStatus.OK);
    }

    @Override
    @ApiOperation(value = "Пользователи данного форума", notes = "Получение списка пользователей, у которых есть пост или ветка обсуждения в данном форуме. Пользователи выводятся отсортированные по nickname в порядке возрастания. Порядок сотрировки должен соответсвовать побайтовому сравнение в нижнем регистре. ", response = Users.class, tags={  })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Информация о пользователях форума. ", response = Users.class),
            @ApiResponse(code = 404, message = "Форум отсутсвует в системе. ", response = Users.class) })
    @RequestMapping(value = "/{slug}/users",
            produces = { "application/json" },
            method = RequestMethod.GET)
    public ResponseEntity<?> forumGetUsers(@ApiParam(value = "Идентификатор форума.",required=true ) @PathVariable("slug") String slug,
    @Min(1) @Max(10000) @ApiParam(value = "Максимальное кол-во возвращаемых записей.", defaultValue = "100") @RequestParam(value = "limit", required = false, defaultValue="100") BigDecimal limit,
    @ApiParam(value = "Идентификатор пользователя, с которого будут выводиться пользоватли (пользователь с данным идентификатором в результат не попадает). ") @RequestParam(value = "since", required = false) String since,
    @ApiParam(value = "Флаг сортировки по убыванию. ") @RequestParam(value = "desc", required = false) Boolean desc) {

        ArrayList<User> users = null;

        try {
            if (since == null) {
                if (desc == null) {
                    users = (ArrayList<User>)jdbcTemplate.query(SEARCH_FORUM_USERS, new Object[] { slug, limit}, new UsersMapper());
                } else {
                    users = (ArrayList<User>)jdbcTemplate.query(SEARCH_FORUM_THREADS_DESC, new Object[] { slug, limit}, new UsersMapper());
                }
            } else {
                if (desc == null) {
                    users = (ArrayList<User>)jdbcTemplate.query(SEARCH_FORUM_THREADS_DATE_LIMIT, new Object[] { slug, limit, since}, new UsersMapper());
                } else {
                    users = (ArrayList<User>)jdbcTemplate.query(SEARCH_FORUM_THREADS_DATE_LIMIT_DESC, new Object[] { slug, limit, since}, new UsersMapper());
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Error error = new Error();
            error.setMessage("Форум отсутсвует в системе.");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Override
    @ApiOperation(value = "Создание ветки", notes = "Добавление новой ветки обсуждения на форум. ", response = Thread.class, tags={  })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Ветка обсуждения успешно создана. Возвращает данные созданной ветки обсуждения. ", response = Thread.class),
            @ApiResponse(code = 404, message = "Автор ветки или форум не найдены. ", response = Thread.class),
            @ApiResponse(code = 409, message = "Ветка обсуждения уже присутсвует в базе данных. Возвращает данные ранее созданной ветки обсуждения. ", response = Thread.class) })
    @RequestMapping(value = "/{slug}/create",
            produces = { "application/json" },
            consumes = { "application/json" },
            method = RequestMethod.POST)
    public ResponseEntity<?> threadCreate(@ApiParam(value = "Идентификатор форума.",required=true ) @PathVariable("slug") String slug,
    @ApiParam(value = "Данные ветки обсуждения." ,required=true ) @RequestBody Thread thread) {
        Forum forumSearchResult = null;
        try {
            forumSearchResult = jdbcTemplate.queryForObject(SEARCH_FORUM_BY_SLUG, new Object[] { slug }, new ForumMapper());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (forumSearchResult == null) {
            return new ResponseEntity<>(new Error("Автор ветки или форум не найдены."), HttpStatus.NOT_FOUND);
        }
        Thread threadSearchResult = null;
        if (thread.getSlug() == null) {
            thread.generateSlug();
        }
        try {
            threadSearchResult = jdbcTemplate.queryForObject(SEARCH_TRHEAD_BY_FORUM_AND_THREAD_SLUGS, new Object[] { slug, thread.getSlug() }, new ThreadsMapper());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (threadSearchResult != null) {
            return new ResponseEntity<>(threadSearchResult, HttpStatus.CONFLICT);
        }
        long user_id = 0;
        try {
            user_id = jdbcTemplate.queryForObject(SEARCH_USER_BY_NICKNAME_QUERY, Long.class, thread.getAuthor());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new Error("Автор ветки или форум не найдены."), HttpStatus.NOT_FOUND);
        }
        long thread_id = 0;
        try {
            if (user_id != 0) {
                long forum_id = 0;
                forum_id = jdbcTemplate.queryForObject(SEARCH_FORIM_ID_BY_SLUG, Long.class, slug);
                jdbcTemplate.update(CREATE_THREAD_QUERY, thread.getTitle(), user_id, forum_id, thread.getMessage(), thread.getSlug());
                thread_id = jdbcTemplate.queryForObject("SELECT id FROM threads WHERE slug = ?", Long.class, thread.getSlug());
            }
            else throw new Exception();
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        thread.setForum(forumSearchResult.getSlug());
        thread.setVotes(new BigDecimal(0));
        thread.setId(new BigDecimal(thread_id));
        return new ResponseEntity<>(thread, HttpStatus.CREATED);
    }
}
