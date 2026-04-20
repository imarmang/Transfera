package com.example.transfera.service.profile;

import com.example.transfera.domain.profile.Profile;
import com.example.transfera.domain.profile.ProfileRepository;
import com.example.transfera.domain.user.UserCredentials;
import com.example.transfera.dto.ProfileDTO.SearchProfileDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith( MockitoExtension.class )
class SearchProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private SearchProfileService searchProfileService;

    // ─── Helper builders ──────────────────────────────────────────────────────

    private Profile buildProfile( String userName, String firstName, String lastName ) {
        UserCredentials user = new UserCredentials( userName + "@example.com", "password" );
        return new Profile( userName, firstName, lastName, "1234567890", user );
    }

    // ─── Unit Tests ───────────────────────────────────────────────────────────

    @Test
    void execute_happyPath_returnsMatchingProfiles() {
        // Verifies that matching profiles are returned correctly.
        List<Profile> profiles = List.of(
                buildProfile( "johndoe", "John", "Doe" ),
                buildProfile( "johnson", "Johnson", "Williams" )
        );

        when( profileRepository.findTop10ByUserNameStartingWithIgnoreCase( "jo" ) )
                .thenReturn( profiles );

        ResponseEntity<List<SearchProfileDTO>> response = searchProfileService.execute( "jo" );

        assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
        assertThat( response.getBody() ).hasSize( 2 );
        assertThat( response.getBody().get( 0 ).getUserName() ).isEqualTo( "johndoe" );
        assertThat( response.getBody().get( 1 ).getUserName() ).isEqualTo( "johnson" );
    }

    @Test
    void execute_noMatches_returnsEmptyList() {
        // Verifies that an empty list is returned when no profiles match.
        when( profileRepository.findTop10ByUserNameStartingWithIgnoreCase( "xyz" ) )
                .thenReturn( List.of() );

        ResponseEntity<List<SearchProfileDTO>> response = searchProfileService.execute( "xyz" );

        assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
        assertThat( response.getBody() ).isEmpty();
    }

    @Test
    void execute_emptyUsername_returnsEmptyListWithoutCallingRepository() {
        // Verifies that an empty query returns early without hitting the DB.
        ResponseEntity<List<SearchProfileDTO>> response = searchProfileService.execute( "" );

        assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
        assertThat( response.getBody() ).isEmpty();
        verify( profileRepository, never() ).findTop10ByUserNameStartingWithIgnoreCase( any() );
    }

    @Test
    void execute_blankUsername_returnsEmptyListWithoutCallingRepository() {
        // Verifies that a blank/whitespace query returns early without hitting the DB.
        ResponseEntity<List<SearchProfileDTO>> response = searchProfileService.execute( "   " );

        assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
        assertThat( response.getBody() ).isEmpty();
        verify( profileRepository, never() ).findTop10ByUserNameStartingWithIgnoreCase( any() );
    }

    @Test
    void execute_nullUsername_returnsEmptyListWithoutCallingRepository() {
        // Verifies that a null query returns early without hitting the DB.
        ResponseEntity<List<SearchProfileDTO>> response = searchProfileService.execute( null );

        assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
        assertThat( response.getBody() ).isEmpty();
        verify( profileRepository, never() ).findTop10ByUserNameStartingWithIgnoreCase( any() );
    }

    @Test
    void execute_mapsProfileFieldsCorrectly() {
        // Verifies that userName, firstName, lastName are mapped correctly to the DTO.
        Profile profile = buildProfile( "arman", "Arman", "Gasparyan" );

        when( profileRepository.findTop10ByUserNameStartingWithIgnoreCase( "ar" ) )
                .thenReturn( List.of( profile ) );

        ResponseEntity<List<SearchProfileDTO>> response = searchProfileService.execute( "ar" );

        SearchProfileDTO dto = response.getBody().get( 0 );
        assertThat( dto.getUserName() ).isEqualTo( "arman" );
        assertThat( dto.getFirstName() ).isEqualTo( "Arman" );
        assertThat( dto.getLastName() ).isEqualTo( "Gasparyan" );
    }

    @Test
    void execute_returnsMaxTenResults() {
        // Verifies that even if repository returns 10 results, all are mapped correctly.
        List<Profile> profiles = List.of(
                buildProfile( "user1", "User", "One" ),
                buildProfile( "user2", "User", "Two" ),
                buildProfile( "user3", "User", "Three" ),
                buildProfile( "user4", "User", "Four" ),
                buildProfile( "user5", "User", "Five" ),
                buildProfile( "user6", "User", "Six" ),
                buildProfile( "user7", "User", "Seven" ),
                buildProfile( "user8", "User", "Eight" ),
                buildProfile( "user9", "User", "Nine" ),
                buildProfile( "user10", "User", "Ten" )
        );

        when( profileRepository.findTop10ByUserNameStartingWithIgnoreCase( "user" ) )
                .thenReturn( profiles );

        ResponseEntity<List<SearchProfileDTO>> response = searchProfileService.execute( "user" );

        assertThat( response.getBody() ).hasSize( 10 );
    }

    @Test
    void execute_neverCallsRepositoryMoreThanOnce() {
        // Verifies that the repository is called exactly once per request.
        when( profileRepository.findTop10ByUserNameStartingWithIgnoreCase( "jo" ) )
                .thenReturn( List.of() );

        searchProfileService.execute( "jo" );

        verify( profileRepository, times( 1 ) )
                .findTop10ByUserNameStartingWithIgnoreCase( "jo" );
    }

    @Test
    void execute_concurrentSearchesSameQuery_allSucceed() throws InterruptedException {
        // Verifies that 10 concurrent searches with the same query all succeed correctly.
        int threadCount = 10;

        List<Profile> profiles = List.of(
                buildProfile( "johndoe", "John", "Doe" ),
                buildProfile( "johnson", "Johnson", "Williams" )
        );

        when( profileRepository.findTop10ByUserNameStartingWithIgnoreCase( "jo" ) )
                .thenReturn( profiles );

        ExecutorService executor = Executors.newFixedThreadPool( threadCount );
        CountDownLatch latch = new CountDownLatch( threadCount );
        AtomicInteger successCount = new AtomicInteger( 0 );
        AtomicInteger failureCount = new AtomicInteger( 0 );

        for ( int i = 0; i < threadCount; i++ ) {
            executor.submit( () -> {
                try {
                    ResponseEntity<List<SearchProfileDTO>> response =
                            searchProfileService.execute( "jo" );
                    assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
                    assertThat( response.getBody() ).hasSize( 2 );
                    successCount.incrementAndGet();
                } catch ( Exception e ) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            } );
        }

        latch.await( 10, TimeUnit.SECONDS );
        executor.shutdown();

        assertThat( successCount.get() ).isEqualTo( threadCount );
        assertThat( failureCount.get() ).isZero();
    }

    @Test
    void execute_concurrentSearchesDifferentQueries_isolatedCorrectly() throws InterruptedException {
        // Verifies that 5 concurrent searches with different queries return correct isolated results.
        int threadCount = 5;

        List<String> queries = List.of( "jo", "ar", "ta", "mi", "sa" );

        for ( String query : queries ) {
            when( profileRepository.findTop10ByUserNameStartingWithIgnoreCase( query ) )
                    .thenReturn( List.of( buildProfile( query + "user", "First", "Last" ) ) );
        }

        ExecutorService executor = Executors.newFixedThreadPool( threadCount );
        CountDownLatch latch = new CountDownLatch( threadCount );
        AtomicInteger successCount = new AtomicInteger( 0 );
        List<String> results = new CopyOnWriteArrayList<>();

        for ( int i = 0; i < threadCount; i++ ) {
            final String query = queries.get( i );
            executor.submit( () -> {
                try {
                    ResponseEntity<List<SearchProfileDTO>> response =
                            searchProfileService.execute( query );
                    results.add( response.getBody().get( 0 ).getUserName() );
                    successCount.incrementAndGet();
                } catch ( Exception e ) {
                    // silent
                } finally {
                    latch.countDown();
                }
            } );
        }

        latch.await( 10, TimeUnit.SECONDS );
        executor.shutdown();

        assertThat( successCount.get() ).isEqualTo( threadCount );
        // Each query returns the correct username for its query
        assertThat( results ).containsExactlyInAnyOrder(
                "jouser", "aruser", "tauser", "miuser", "sauser"
        );
    }

    @Test
    void execute_concurrentEmptyQueries_neverCallRepository() throws InterruptedException {
        // Verifies that concurrent empty queries never hit the repository.
        int threadCount = 8;

        ExecutorService executor = Executors.newFixedThreadPool( threadCount );
        CountDownLatch latch = new CountDownLatch( threadCount );
        AtomicInteger successCount = new AtomicInteger( 0 );

        for ( int i = 0; i < threadCount; i++ ) {
            executor.submit( () -> {
                try {
                    ResponseEntity<List<SearchProfileDTO>> response =
                            searchProfileService.execute( "" );
                    assertThat( response.getBody() ).isEmpty();
                    successCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            } );
        }

        latch.await( 10, TimeUnit.SECONDS );
        executor.shutdown();

        assertThat( successCount.get() ).isEqualTo( threadCount );
        verify( profileRepository, never() )
                .findTop10ByUserNameStartingWithIgnoreCase( any() );
    }
}