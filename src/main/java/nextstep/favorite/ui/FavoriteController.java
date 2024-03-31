package nextstep.favorite.ui;

import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import nextstep.auth.application.domain.CustomUserPrincipal;
import nextstep.auth.application.ui.AuthenticationPrincipal;
import nextstep.favorite.application.dto.FavoriteRequest;
import nextstep.favorite.application.dto.FavoriteResponse;
import nextstep.favorite.application.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class FavoriteController {

    private final FavoriteService favoriteService;


    @PostMapping("/favorites")
    public ResponseEntity<Void> createFavorite(
        @RequestBody FavoriteRequest request,
        @AuthenticationPrincipal CustomUserPrincipal userPrincipal
    ) {
        Long id = favoriteService.createFavorite(userPrincipal, request);
        return ResponseEntity.created(URI.create("/favorites/" + id)).build();
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<FavoriteResponse>> getFavorites(@AuthenticationPrincipal
    CustomUserPrincipal userPrincipal) {
        List<FavoriteResponse> favorites = favoriteService.findFavorites(userPrincipal);
        return ResponseEntity.ok().body(favorites);
    }

    @DeleteMapping("/favorites/{id}")
    public ResponseEntity<Void> deleteFavorite(@AuthenticationPrincipal CustomUserPrincipal userPrincipal,
        @PathVariable Long id) {
        favoriteService.deleteFavorite(userPrincipal, id);
        return ResponseEntity.noContent().build();
    }
}
