package nextstep.domain.subway.domain;

import nextstep.domain.member.domain.Member;
import nextstep.domain.subway.dto.PathResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FavoritePathRepository extends JpaRepository<FavoritePath,Long> {
    @Query("select f from FavoritePath f where f.memberId = ?1")
    List<FavoritePath> findAllByMemberId(Long memberId);

    FavoritePath findOneById(Long favoriteId);

    Optional<FavoritePath> findByIdAndMemberId(Long favoriteId, Long memberId);
}