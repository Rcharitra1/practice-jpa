package dmit2015.repository;

import config.ApplicationConfig;
import dmit2015.entity.Movie;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(ArquillianExtension.class)
class MovieRepositoryArquillianIT {
    @Inject
    private MovieRepository _movieRepository;

    @Deployment
    public static WebArchive createDeployment() {
        PomEquippedResolveStage pomFile = Maven.resolver().loadPomFromFile("pom.xml");

        return ShrinkWrap.create(WebArchive.class,"test.war")
//                .addAsLibraries(pomFile.resolve("groupId:artifactId:version").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("com.h2database:h2:1.4.200").withTransitivity().asFile())
                // .addAsLibraries(pomFile.resolve("com.microsoft.sqlserver:mssql-jdbc:8.4.1.jre11").withTransitivity().asFile())
                // .addAsLibraries(pomFile.resolve("com.oracle.database.jdbc:ojdbc10:19.9.0.0").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("org.hamcrest:hamcrest:2.2").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("org.hibernate:hibernate-core:5.3.20.Final").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("org.hibernate.validator:hibernate-validator:6.2.0.Final").withTransitivity().asFile())
                .addClass(ApplicationConfig.class)
                .addClasses(Movie.class, MovieRepository.class)
                .addAsResource("META-INF/persistence.xml")
                .addAsResource("META-INF/sql/import-data.sql")
                .addAsWebInfResource(EmptyAsset.INSTANCE,"beans.xml");
    }

    @Test
    @Transactional(TransactionMode.ROLLBACK)
    void shouldCreate()
    {
        Movie newMovie = new Movie();

        newMovie.setMovieName("Harry Met Sally");
        LocalDateTime timeToTest = LocalDateTime.now();
        newMovie.setReleaseDate(LocalDate.parse("2020-12-12"));
        newMovie.setRecordCreationDate(timeToTest);
        newMovie.setGenre("thriller");

        _movieRepository.create(newMovie);

        Optional<Movie> movieOptional = _movieRepository.findOne(newMovie.getMovieId());
        assertTrue(movieOptional.isPresent());

        Movie movieToTest = new Movie();
        movieToTest = movieOptional.get();

        assertEquals("Harry Met Sally", movieToTest.getMovieName());
        assertEquals("thriller", movieToTest.getGenre());
        assertTrue(LocalDateTime.now().isAfter(movieToTest.getRecordCreationDate()));
        assertTrue(timeToTest.isBefore(movieToTest.getRecordCreationDate()));

        assertEquals(LocalDate.parse("2020-12-12"), movieToTest.getReleaseDate());


    }

    @Test
    @Transactional(TransactionMode.ROLLBACK)
    void shouldUpdate()
    {
        final long Id = 2L;
        Optional<Movie> movieOptional = _movieRepository.findOne(Id);
        assertTrue(movieOptional.isPresent());
        Movie movieToTest = movieOptional.get();

        movieToTest.setGenre("thriller");
        movieToTest.setMovieName("Ju On");
        movieToTest.setReleaseDate(LocalDate.parse("2020-12-30"));

        _movieRepository.update(movieToTest);

        Optional<Movie> movieOptionalDb = _movieRepository.findOne(Id);
        assertTrue(movieOptionalDb.isPresent());
        Movie updatedMovie = movieOptionalDb.get();

        assertEquals("thriller", updatedMovie.getGenre());
        assertEquals("Ju On", updatedMovie.getMovieName());
        assertEquals(LocalDate.parse("2020-12-30"), updatedMovie.getReleaseDate());
    }

    @Test
    @Transactional(TransactionMode.ROLLBACK)

    void shouldDelete()
    {
        final long Id = 2L;

        Optional<Movie> movieOptional = _movieRepository.findOne(Id);
        assertTrue(movieOptional.isPresent());

        Movie toDelete = movieOptional.get();

        _movieRepository.remove(toDelete);

        Optional<Movie> optionalMovie = _movieRepository.findOne(Id);
        assertTrue(optionalMovie.isEmpty());
    }

    @Test
    void shouldFindOne()
    {
        final long Id = 1L;

        Optional<Movie> optionalMovie = _movieRepository.findOne(Id);
        assertTrue(optionalMovie.isPresent());
        Movie movie = new Movie();
        movie = optionalMovie.get();

        assertEquals("Harry Met Sally", movie.getMovieName());
        assertEquals("romance", movie.getGenre());
        assertEquals(LocalDate.parse("2020-12-12"), movie.getReleaseDate());
    }
    @Test
    void shouldFindAll()
    {
        List<Movie> allMovies = _movieRepository.findAll();

        assertEquals(3, allMovies.size());
        Movie firstMovie = allMovies.get(0);

        assertEquals("Harry Met Sally", firstMovie.getMovieName());
        assertEquals("romance", firstMovie.getGenre());
        assertEquals(LocalDate.parse("2020-12-12"), firstMovie.getReleaseDate());

        Movie lastMovie = allMovies.get(2);
        assertEquals("Ring", lastMovie.getMovieName());
        assertEquals("horror", lastMovie.getGenre());
        assertEquals(LocalDate.parse("2020-12-01"), lastMovie.getReleaseDate());
    }

    @Test
    @Transactional(TransactionMode.ROLLBACK)

    void shouldThrowException()
    {
        Movie newMovie = new Movie();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()-> newMovie.setGenre("LION"));

        final long Id = 2L;
        Optional<Movie> optionalMovie = _movieRepository.findOne(Id);
        Movie existingMovie = optionalMovie.get();

        IllegalArgumentException ee= assertThrows(IllegalArgumentException.class, ()-> existingMovie.setGenre("LION"));
    }
}