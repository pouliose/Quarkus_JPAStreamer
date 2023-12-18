package org.acme.app.recources;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.app.model.Film;
import org.acme.app.repository.FilmRepository;

import java.util.Optional;
import java.util.stream.Collectors;

@Path("/")
public class FilmResource {
    @Inject
    FilmRepository filmRepository;

    @GET
    @Path("/welcome")
    @Produces(MediaType.TEXT_PLAIN)
    public String welcome(){
        return "Welcome";
    }

    @GET
    @Path("/film/{filmId}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getFilmByFilmId(short filmId){
        Optional<Film> film = filmRepository.getFilm(filmId);
        return film.isPresent() ? film.get().getTitle() : "No film was found";
    }

    @GET
    @Path("/pagedFilms/{page}/{minLength}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getPagedFilms(long page, short minLength){
        return filmRepository.paged(page,minLength)
                .map(f->String.format("%s (%d min)", f.getTitle(), f.getLength()))
                .collect(Collectors.joining("\n"));
    }
    @GET
    @Path("/actors/{startsWith}/{minLength}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getActorsByFilmPrefixAndLength(String startsWith, short minLength){
        return filmRepository.actors(startsWith, minLength)
                .map(f->String.format("%s (%d min): %s",
                        f.getTitle(),
                        f.getLength(),
                        f.getActors().stream()
                                .map(a->String.format("%s %s", a.getFirstName(), a.getLastName()))
                .collect(Collectors.joining(",")))).collect(Collectors.joining("\n"));
    }

    @PUT
    @Path("/update/{minLength}/{rentalRate}")
    @Produces(MediaType.TEXT_PLAIN)
    public String updateRentalRate(short minLength, float rentalRate){
        filmRepository.updateRentalRate(minLength, rentalRate);
        return filmRepository.getFilmsByLength(minLength)
                .map(f->String.format("%s (%d min): $%f",
                        f.getTitle(),
                        f.getLength(),
                        f.getRentalRate()))
                .collect(Collectors.joining("\n"));
    }

}
