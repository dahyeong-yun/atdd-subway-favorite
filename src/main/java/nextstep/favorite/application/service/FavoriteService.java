package nextstep.favorite.application.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import nextstep.auth.application.domain.CustomUserPrincipal;
import nextstep.common.error.exception.BadRequestException;
import nextstep.common.error.exception.NotFoundException;
import nextstep.common.error.exception.UnAuthorizedException;
import nextstep.favorite.application.dto.FavoriteRequest;
import nextstep.favorite.application.dto.FavoriteResponse;
import nextstep.favorite.domain.Favorite;
import nextstep.favorite.domain.FavoriteRepository;
import nextstep.member.domain.Member;
import nextstep.member.domain.MemberRepository;
import nextstep.subway.application.service.StationService;
import nextstep.subway.domain.entity.PathFinder;
import nextstep.subway.domain.entity.Station;
import nextstep.subway.domain.repository.LineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FavoriteService {

    private final StationService stationService;
    private final MemberRepository memberRepository;
    private final LineRepository lineRepository;
    private final FavoriteRepository favoriteRepository;


    @Transactional
    public Long createFavorite(CustomUserPrincipal userPrincipal, FavoriteRequest request) {
        Station source = stationService.getStationById(request.getSource());
        Station target = stationService.getStationById(request.getTarget());
        Member member = getMember(userPrincipal);
        PathFinder pathFinder = new PathFinder(lineRepository.findAll());
        if (!pathFinder.isValidPath(source, target)) {
            throw new BadRequestException("invalid favorite info");
        }
        Favorite favorite = favoriteRepository.save(Favorite.of(member.getId(), source, target));
        return favorite.getId();
    }


    public List<FavoriteResponse> findFavorites(CustomUserPrincipal userPrincipal) {
        Member member = getMember(userPrincipal);
        List<Favorite> favorites = favoriteRepository.findAllByMemberId(member.getId());
        return favorites.stream()
            .map(FavoriteResponse::new)
            .collect(Collectors.toList());
    }


    @Transactional
    public void deleteFavorite(CustomUserPrincipal userPrincipal, Long id) {
        Member member = getMember(userPrincipal);
        Favorite favorite = favoriteRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("not found favorite"));
        if (!favorite.isOwner(member.getId())) {
            throw new UnAuthorizedException();
        }
        favoriteRepository.deleteByIdAndMemberId(id, member.getId());
    }

    private Member getMember(CustomUserPrincipal userPrincipal) {
        return memberRepository.findByEmail(userPrincipal.getUserDetail().getId())
            .orElseThrow(() -> new NotFoundException("not found member"));
    }
}
