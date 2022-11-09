package ru.swamptea.repository;

import org.springframework.stereotype.Repository;
import ru.swamptea.exception.NotFoundException;
import ru.swamptea.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepository {
    private final ConcurrentHashMap<Long, Post> posts = new ConcurrentHashMap<>();
    private final AtomicLong count = new AtomicLong(0);

    public List<Post> all() {
        List<Post> all = new ArrayList<>();
        for (Post p : posts.values()){
            if(!p.isRemoved()){
                all.add(p);
            }
        }
        return all;
    }

    public Optional<Post> getById(long id) {
        if(posts.get(id) != null && !posts.get(id).isRemoved()){
            return Optional.ofNullable(posts.get(id));
        }
        else throw new NotFoundException("Post not found");
    }

    public Post save(Post post) {
        if (post.getId() == 0) {
            post.setId(count.incrementAndGet());
            posts.put(post.getId(), post);
        } else {
            if (posts.containsKey(post.getId()) && !posts.get(post.getId()).isRemoved()) {
                posts.replace(post.getId(), post);
            } else throw new NotFoundException("Post not found");
        }
        return post;
    }

    public void removeById(long id) {
        if (posts.containsKey(id)) {
            posts.get(id).setRemoved(true);
        }
    }
}
