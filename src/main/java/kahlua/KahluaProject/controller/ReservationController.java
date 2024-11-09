package kahlua.KahluaProject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kahlua.KahluaProject.apipayload.ApiResponse;
import kahlua.KahluaProject.dto.reservation.request.ReservationProceedRequest;
import kahlua.KahluaProject.dto.reservation.request.ReservationRequest;
import kahlua.KahluaProject.dto.reservation.response.ReservationListResponse;
import kahlua.KahluaProject.dto.reservation.response.ReservationResponse;
import kahlua.KahluaProject.security.AuthDetails;
import kahlua.KahluaProject.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@Tag(name = "동방 예약", description = "동방 예약 기능 관련 API")
@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // 예약 시간 선택한 사람에게 우선권 부여
    @MessageMapping("/reserve.proceed/{date}")
    @SendTo("/topic/public/{date}")
    public ReservationResponse proceed(@DestinationVariable String reservationDate,
                                       @Header("simpSessionAttributes") Map<String, Object> simpSessionAttributes,
                                       @Payload ReservationProceedRequest reservationProceedRequest) {

        return reservationService.proceed(reservationProceedRequest, reservationDate, simpSessionAttributes);
    }

    // 예약 확정 후 예약내역 DB에 저장
    @MessageMapping("/reserve.complete/{date}")
    @SendTo("/topic/public/{date}")
    public ReservationResponse complete(@DestinationVariable String reservationDate,
                                       @Header("simpSessionAttributes") Map<String, Object> simpSessionAttributes,
                                       @Payload ReservationRequest reservationRequest) {

        return reservationService.save(reservationRequest, reservationDate, simpSessionAttributes);
    }

    @GetMapping("/v1/reservation")
    @Operation(summary = "날짜별 예약내역 목록 조회", description = "지정한 날짜에 해당하는 예약내역 목록을 조회합니다." +
            "<br> 쿼리 파라미터 날짜 형식은 yyyy-MM-dd 입니다")
    public ApiResponse<ReservationListResponse> getReservationListByDate(@RequestParam(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

        return ApiResponse.onSuccess(reservationService.getByDate(date));
    }

    @GetMapping("/v1/reservation/check")
    @Operation(summary = "사용자 예약내역 조회", description = "사용자의 전체 예약내역을 조회합니다.")
    public ApiResponse<ReservationListResponse> getReservationList(@AuthenticationPrincipal AuthDetails authDetails) {

        return ApiResponse.onSuccess(reservationService.getByUser(authDetails.user()));
    }

    @DeleteMapping("/v1/reservation/check/{reservationId}")
    @Operation(summary = "예약내역 삭제", description = "지정한 예약내역을 삭제합니다.")
    public ApiResponse<?> delete(@PathVariable Long reservationId) {

        reservationService.delete(reservationId);
        return ApiResponse.onSuccess("예약내역 삭제 성공");
    }
}
