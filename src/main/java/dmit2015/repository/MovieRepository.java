package dmit2015.repository;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import dmit2015.entity.Movie;
import org.jboss.arquillian.test.spi.ExceptionProxy;

@ApplicationScoped
@Transactional

public class MovieRepository {

    @PersistenceContext
    EntityManager em;

    public void create (Movie movie)
    {
        em.persist(movie);
    }

    public Optional<Movie> findOne (long Id)
    {
        Optional<Movie> optionalMovie = Optional.empty();
        try
        {
            Movie singleQuery = em.find(Movie.class, Id);
            if(singleQuery !=null)
            {
                optionalMovie=Optional.of(singleQuery);
            }


        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return optionalMovie;
    }

    public List<Movie> findAll()
    {
        return em.createQuery(
            "SELECT m FROM Movie m  "
            , Movie.class)
            .getResultList();
    }

    public void update(Movie movie)
    {
        Optional<Movie> movieOptional = findOne(movie.getMovieId());
        if(movieOptional.isPresent())
        {
            Movie movieToUpdate = new Movie();

            movieToUpdate = movieOptional.get();
            movieToUpdate.setMovieName(movie.getMovieName());
            movieToUpdate.setGenre(movie.getGenre());
            movieToUpdate.setReleaseDate(movie.getReleaseDate());

            em.merge(movieToUpdate);
            em.flush();
        }
    }

    public void remove(long Id)
    {
        Optional<Movie> movieOptional = findOne(Id);

        if(movieOptional.isPresent())
        {
            Movie movieToDelete = movieOptional.get();
            remove(movieToDelete);

        }
    }

    public void remove(Movie movieToDelete)
    {
        em.remove(em.merge(movieToDelete));
        em.flush();
    }





}
