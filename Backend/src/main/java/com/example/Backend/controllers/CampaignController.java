package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.dtos.campaign.CampaignRequest;
import com.example.Backend.dtos.campaign.CampaignResponse;
import com.example.Backend.services.CampaignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Campaign Management", description = "API quản lý chiến dịch marketing")
public class CampaignController {

    private final CampaignService campaignService;

    @PostMapping
    @Operation(summary = "Tạo chiến dịch mới", description = "Tạo một chiến dịch marketing mới trong hệ thống")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ResponseData<CampaignResponse>> createCampaign(
            @Valid @RequestBody CampaignRequest request) {
        try {
            log.info("Tạo chiến dịch mới: {}", request.getName());
            // Assuming current user ID = 1 for demo, should get from SecurityContext
            CampaignResponse createdCampaign = campaignService.createCampaign(request, 1L);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseData.<CampaignResponse>builder()
                            .status(HttpStatus.CREATED.value())
                            .message("Tạo chiến dịch thành công")
                            .data(createdCampaign)
                            .build());
        } catch (Exception e) {
            log.error("Lỗi tạo chiến dịch: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<CampaignResponse>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi tạo chiến dịch: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật chiến dịch", description = "Cập nhật thông tin chiến dịch theo ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ResponseData<CampaignResponse>> updateCampaign(
            @Parameter(description = "ID của chiến dịch") @PathVariable @Min(1) Long id,
            @Valid @RequestBody CampaignRequest request) {
        try {
            log.info("Cập nhật chiến dịch ID: {}", id);
            CampaignResponse updatedCampaign = campaignService.updateCampaign(id, request);
            return ResponseEntity.ok(ResponseData.<CampaignResponse>builder()
                    .status(HttpStatus.OK.value())
                    .message("Cập nhật chiến dịch thành công")
                    .data(updatedCampaign)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi cập nhật chiến dịch ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<CampaignResponse>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi cập nhật chiến dịch: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin chiến dịch theo ID", description = "Lấy chi tiết thông tin một chiến dịch")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('SALES')")
    public ResponseEntity<ResponseData<CampaignResponse>> getCampaignById(
            @Parameter(description = "ID của chiến dịch") @PathVariable @Min(1) Long id) {
        try {
            CampaignResponse campaign = campaignService.getCampaignById(id);
            return ResponseEntity.ok(ResponseData.<CampaignResponse>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy thông tin chiến dịch thành công")
                    .data(campaign)
                    .build());
        } catch (Exception e) {
            log.error("Không tìm thấy chiến dịch ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<CampaignResponse>builder()
                            .status(HttpStatus.NOT_FOUND.value())
                            .message("Không tìm thấy chiến dịch: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách chiến dịch", description = "Lấy danh sách tất cả chiến dịch với phân trang")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('SALES')")
    public ResponseEntity<ResponseData<Page<CampaignResponse>>> getAllCampaigns(
            @Parameter(description = "Số trang (bắt đầu từ 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sắp xếp theo trường") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<CampaignResponse> campaigns = campaignService.getAllCampaigns(pageable);
            return ResponseEntity.ok(ResponseData.<Page<CampaignResponse>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy danh sách chiến dịch thành công")
                    .data(campaigns)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi lấy danh sách chiến dịch: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Page<CampaignResponse>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi lấy danh sách chiến dịch: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Lấy danh sách chiến dịch theo trạng thái", description = "Lấy danh sách chiến dịch theo trạng thái cụ thể")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('SALES')")
    public ResponseEntity<ResponseData<List<CampaignResponse>>> getCampaignsByStatus(
            @Parameter(description = "Trạng thái chiến dịch") @PathVariable String status) {
        try {
            List<CampaignResponse> campaigns = campaignService.getCampaignsByStatus(status);
            return ResponseEntity.ok(ResponseData.<List<CampaignResponse>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy danh sách chiến dịch theo trạng thái thành công")
                    .data(campaigns)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi lấy danh sách chiến dịch theo trạng thái: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<List<CampaignResponse>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi lấy danh sách chiến dịch: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Lấy danh sách chiến dịch theo loại", description = "Lấy danh sách chiến dịch theo loại cụ thể")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('SALES')")
    public ResponseEntity<ResponseData<List<CampaignResponse>>> getCampaignsByType(
            @Parameter(description = "Loại chiến dịch") @PathVariable String type) {
        try {
            List<CampaignResponse> campaigns = campaignService.getCampaignsByType(type);
            return ResponseEntity.ok(ResponseData.<List<CampaignResponse>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy danh sách chiến dịch theo loại thành công")
                    .data(campaigns)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi lấy danh sách chiến dịch theo loại: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<List<CampaignResponse>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi lấy danh sách chiến dịch: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/active")
    @Operation(summary = "Lấy danh sách chiến dịch đang hoạt động", description = "Lấy danh sách tất cả chiến dịch đang được kích hoạt")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('SALES')")
    public ResponseEntity<ResponseData<List<CampaignResponse>>> getActiveCampaigns() {
        try {
            List<CampaignResponse> activeCampaigns = campaignService.getActiveCampaigns();
            return ResponseEntity.ok(ResponseData.<List<CampaignResponse>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy danh sách chiến dịch hoạt động thành công")
                    .data(activeCampaigns)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi lấy danh sách chiến dịch hoạt động: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<List<CampaignResponse>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi lấy danh sách chiến dịch hoạt động: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/{id}/start")
    @Operation(summary = "Bắt đầu chiến dịch", description = "Kích hoạt chiến dịch từ trạng thái DRAFT")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ResponseData<Void>> startCampaign(
            @Parameter(description = "ID của chiến dịch") @PathVariable @Min(1) Long id) {
        try {
            log.info("Bắt đầu chiến dịch ID: {}", id);
            campaignService.startCampaign(id);
            return ResponseEntity.ok(ResponseData.<Void>builder()
                    .status(HttpStatus.OK.value())
                    .message("Bắt đầu chiến dịch thành công")
                    .build());
        } catch (Exception e) {
            log.error("Lỗi bắt đầu chiến dịch ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Void>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi bắt đầu chiến dịch: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/{id}/pause")
    @Operation(summary = "Tạm dừng chiến dịch", description = "Tạm dừng chiến dịch đang chạy")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ResponseData<Void>> pauseCampaign(
            @Parameter(description = "ID của chiến dịch") @PathVariable @Min(1) Long id) {
        try {
            log.info("Tạm dừng chiến dịch ID: {}", id);
            campaignService.pauseCampaign(id);
            return ResponseEntity.ok(ResponseData.<Void>builder()
                    .status(HttpStatus.OK.value())
                    .message("Tạm dừng chiến dịch thành công")
                    .build());
        } catch (Exception e) {
            log.error("Lỗi tạm dừng chiến dịch ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Void>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi tạm dừng chiến dịch: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/{id}/complete")
    @Operation(summary = "Hoàn thành chiến dịch", description = "Đánh dấu chiến dịch đã hoàn thành")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ResponseData<Void>> completeCampaign(
            @Parameter(description = "ID của chiến dịch") @PathVariable @Min(1) Long id) {
        try {
            log.info("Hoàn thành chiến dịch ID: {}", id);
            campaignService.completeCampaign(id);
            return ResponseEntity.ok(ResponseData.<Void>builder()
                    .status(HttpStatus.OK.value())
                    .message("Hoàn thành chiến dịch thành công")
                    .build());
        } catch (Exception e) {
            log.error("Lỗi hoàn thành chiến dịch ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Void>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi hoàn thành chiến dịch: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/{id}/targets")
    @Operation(summary = "Thêm khách hàng vào chiến dịch", description = "Thêm danh sách khách hàng mục tiêu vào chiến dịch")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ResponseData<Void>> addTargets(
            @Parameter(description = "ID của chiến dịch") @PathVariable @Min(1) Long id,
            @Valid @RequestBody List<Long> customerIds) {
        try {
            log.info("Thêm {} khách hàng vào chiến dịch ID: {}", customerIds.size(), id);
            campaignService.addTargets(id, customerIds);
            return ResponseEntity.ok(ResponseData.<Void>builder()
                    .status(HttpStatus.OK.value())
                    .message("Thêm khách hàng vào chiến dịch thành công")
                    .build());
        } catch (Exception e) {
            log.error("Lỗi thêm khách hàng vào chiến dịch ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Void>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi thêm khách hàng: " + e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/{id}/targets/{customerId}")
    @Operation(summary = "Xóa khách hàng khỏi chiến dịch", description = "Xóa một khách hàng khỏi danh sách mục tiêu của chiến dịch")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ResponseData<Void>> removeTarget(
            @Parameter(description = "ID của chiến dịch") @PathVariable @Min(1) Long id,
            @Parameter(description = "ID của khách hàng") @PathVariable @Min(1) Long customerId) {
        try {
            log.info("Xóa khách hàng {} khỏi chiến dịch ID: {}", customerId, id);
            campaignService.removeTarget(id, customerId);
            return ResponseEntity.ok(ResponseData.<Void>builder()
                    .status(HttpStatus.OK.value())
                    .message("Xóa khách hàng khỏi chiến dịch thành công")
                    .build());
        } catch (Exception e) {
            log.error("Lỗi xóa khách hàng {} khỏi chiến dịch ID {}: {}", customerId, id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Void>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi xóa khách hàng: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/{id}/targets")
    @Operation(summary = "Lấy danh sách khách hàng mục tiêu", description = "Lấy danh sách tất cả khách hàng mục tiêu của chiến dịch")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('SALES')")
    public ResponseEntity<ResponseData<List<Object>>> getCampaignTargets(
            @Parameter(description = "ID của chiến dịch") @PathVariable @Min(1) Long id) {
        try {
            List<Object> targets = campaignService.getCampaignTargets(id);
            return ResponseEntity.ok(ResponseData.<List<Object>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy danh sách khách hàng mục tiêu thành công")
                    .data(targets)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi lấy danh sách khách hàng mục tiêu cho chiến dịch ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<List<Object>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi lấy danh sách khách hàng: " + e.getMessage())
                            .build());
        }
    }
}
