package ru.otus.hw.data;

import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;

public class CommentTestData {

    public static List<Comment> getExpectedBooksComments(List<Book> books) {
         return List.of(new Comment("id_1", "Comment_1", books.get(0)),
                new Comment("id_2", "Comment_2", books.get(0)),
                new Comment("id_3", "Comment_3", books.get(0)),
                new Comment("id_4", "Comment_4", books.get(1)),
                new Comment("id_5", "Comment_5", books.get(1)),
                new Comment("id_6", "Comment_6", books.get(1)));
    }
}
