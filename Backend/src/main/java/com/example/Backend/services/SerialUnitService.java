package com.example.Backend.services;

import com.example.Backend.dtos.serial.SerialUnitRequest;
import com.example.Backend.dtos.serial.SerialUnitResponse;
import com.example.Backend.exceptions.StockException;
import com.example.Backend.mappers.SerialUnitMapper;
import com.example.Backend.models.SKU;
import com.example.Backend.models.SerialUnit;
import com.example.Backend.repositorys.SKURepository;
import com.example.Backend.repositorys.SerialUnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SerialUnitService {

    private final SerialUnitRepository serialUnitRepository;
    private final SKURepository skuRepository;
    private final SerialUnitMapper serialUnitMapper;

    public SerialUnitResponse createSerialUnit(SerialUnitRequest request) {
        SKU sku = findSkuById(request.getSkuId());

        if (serialUnitRepository.existsByImei(request.getImei())) {
            throw new StockException("IMEI already exists: " + request.getImei());
        }

        SerialUnit serialUnit = SerialUnit.builder()
                .sku(sku)
                .imei(request.getImei())
                .serialNumber(request.getSerialNumber())
                .status(SerialUnit.SerialStatus.IN_STOCK)
                .purchaseDate(request.getPurchaseDate())
                .notes(request.getNotes())
                .build();

        SerialUnit saved = serialUnitRepository.save(serialUnit);
        return serialUnitMapper.toResponse(saved);
    }

    public SerialUnitResponse getSerialUnitById(Long id) {
        SerialUnit serialUnit = findSerialUnitById(id);
        return serialUnitMapper.toResponse(serialUnit);
    }

    public SerialUnitResponse getSerialUnitByImei(String imei) {
        SerialUnit serialUnit = serialUnitRepository.findByImei(imei)
                .orElseThrow(() -> new StockException("Serial unit not found with IMEI: " + imei));
        return serialUnitMapper.toResponse(serialUnit);
    }

    public List<SerialUnitResponse> getSerialUnitsBySkuId(Long skuId) {
        return serialUnitRepository.findBySkuId(skuId).stream()
                .map(serialUnitMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<SerialUnitResponse> getSerialUnitsByStatus(String status) {
        SerialUnit.SerialStatus serialStatus = SerialUnit.SerialStatus.valueOf(status.toUpperCase());
        return serialUnitRepository.findByStatus(serialStatus).stream()
                .map(serialUnitMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<SerialUnitResponse> getAvailableSerialUnits() {
        return serialUnitRepository.findAvailableSerialUnits().stream()
                .map(serialUnitMapper::toResponse)
                .collect(Collectors.toList());
    }

    public void updateStatus(Long id, String status) {
        SerialUnit serialUnit = findSerialUnitById(id);
        SerialUnit.SerialStatus newStatus = SerialUnit.SerialStatus.valueOf(status.toUpperCase());
        serialUnit.setStatus(newStatus);
        serialUnitRepository.save(serialUnit);
    }

    public void markAsSold(Long id) {
        SerialUnit serialUnit = findSerialUnitById(id);
        serialUnit.setStatus(SerialUnit.SerialStatus.SOLD);
        serialUnitRepository.save(serialUnit);
    }

    public Page<SerialUnitResponse> getAllSerialUnits(Pageable pageable) {
        return serialUnitRepository.findAll(pageable)
                .map(serialUnitMapper::toResponse);
    }

    private SerialUnit findSerialUnitById(Long id) {
        return serialUnitRepository.findById(id)
                .orElseThrow(() -> new StockException("Serial unit not found with id: " + id));
    }

    private SKU findSkuById(Long id) {
        return skuRepository.findById(id)
                .orElseThrow(() -> new StockException("SKU not found with id: " + id));
    }
}
