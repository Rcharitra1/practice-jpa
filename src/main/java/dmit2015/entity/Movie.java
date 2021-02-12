package dmit2015.entity;


import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;

@Data
@Entity
@Table(name="movies")
public class Movie implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long movieId;

    @Column(nullable = false)
    @NotEmpty(message="Movie name is a required field")
    @Size(min=1, max=100,message="Movie name allows {min} to {max} characters")
    private String movieName;

    @Column(nullable = false)
    @NotEmpty(message="genre name is a required field")
    private String genre;

    @Column(nullable = false)
    private LocalDate releaseDate;

    @Column(nullable = false)
    private LocalDateTime recordCreationDate;

    @PrePersist
    void prePersist()
    {
        recordCreationDate= LocalDateTime.now();
    }

    public void setGenre(String gen)
    {
        ArrayList<String> allowedValues = new ArrayList<String>();

        allowedValues.add("horror");
        allowedValues.add("thriller");
        allowedValues.add("romance");
        allowedValues.add("period");
        allowedValues.add("action");

        gen = gen.toLowerCase().trim();

        if(allowedValues.indexOf(gen)>=0)
        {
            this.genre= gen;
        }else{
            throw new IllegalArgumentException("only horror, thriller, romance, period and action are allowed values for genre");
        }

    }






}
