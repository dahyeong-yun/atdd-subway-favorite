package nextstep.domain.subway.service;

import nextstep.auth.authentication.LoginMember;
import nextstep.domain.member.domain.LoginMemberImpl;
import nextstep.domain.member.domain.Member;
import nextstep.domain.member.domain.MemberRepository;
import nextstep.domain.subway.domain.FavoritePath;
import nextstep.domain.subway.domain.FavoritePathRepository;
import nextstep.domain.subway.domain.Station;
import nextstep.domain.subway.domain.StationRepository;
import nextstep.domain.subway.dto.FavoritePathRequest;
import nextstep.domain.subway.dto.response.FavoritePathResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoritePathServiceTest {

    private FavoritePathService favoritePathService;

    @Mock
    private FavoritePathRepository favoritePathRepository;

    @Mock
    private StationRepository stationRepository;

    @Mock
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        favoritePathService = new FavoritePathService(favoritePathRepository, stationRepository, memberRepository);
    }

    @DisplayName("즐겨찾기 생성")
    @Test
    void createFavorite() {
        //given
        LoginMemberImpl loginMember = getLoginMember();
        FavoritePathRequest favoritePathRequest = new FavoritePathRequest(1L, 2L);
        FavoritePath favoritePath = getFavoritePath();
        Member member = getMember();
        when(stationRepository.findOneById(1L)).thenReturn(new Station("AStation"));
        when(stationRepository.findOneById(2L)).thenReturn(new Station("BStation"));
        when(memberRepository.findOneById(1L)).thenReturn(member);
        when(favoritePathRepository.save(any())).thenReturn(favoritePath);

        //when
        long favoriteId = favoritePathService.createFavorite(loginMember, favoritePathRequest);

        //then
        assertThat(favoriteId).isEqualTo(favoritePath.getId());
    }


    @DisplayName("즐겨찾기 삭제 - 삭제할 수 없는 즐겨찾기 일 경우")
    @Test
    void deleteFavoriteV2() {
        //given
        Long favoriteId = 1L;
        Long memberId = 1L;
        when(favoritePathRepository.findByIdAndMemberId(favoriteId, memberId)).thenReturn(Optional.empty());

        //when then
        assertThrows(IllegalArgumentException.class, () -> favoritePathService.deleteFavorite(favoriteId, memberId));
    }

    private LoginMemberImpl getLoginMember() {
        LoginMemberImpl loginMember = new LoginMemberImpl(1L, "email@email.com","password", 20);
        return loginMember;
    }

    private Member getMember() {
        Member member = new Member("email@email.com","password", 20);
        ReflectionTestUtils.setField(member, "id", 1L);
        return member;
    }

    private FavoritePath getFavoritePath() {
        FavoritePath favoritePath = new FavoritePath();
        ReflectionTestUtils.setField(favoritePath, "id", 1L);
        return favoritePath;
    }


}