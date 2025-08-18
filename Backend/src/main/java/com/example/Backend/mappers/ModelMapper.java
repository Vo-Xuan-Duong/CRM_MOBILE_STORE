package com.example.Backend.mappers;

import com.example.Backend.dtos.model.*;
import com.example.Backend.models.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ModelMapper {

    /**
     * Chuyển đổi từ ModelCreateDTO sang Model entity
     */
    public Model toEntity(ModelCreateDTO createDTO) {
        if (createDTO == null) {
            return null;
        }

        // Build display spec
        DisplaySpec displaySpec = DisplaySpec.builder()
                .sizeInch(createDTO.getDisplaySizeInch())
                .resolution(createDTO.getDisplayResolution())
                .refreshHz(createDTO.getDisplayRefreshHz())
                .panel(createDTO.getDisplayPanel())
                .brightnessNits(createDTO.getDisplayBrightnessNits())
                .glassProtection(createDTO.getGlassProtection())
                .build();

        // Build camera spec
        CameraSpec cameraSpec = CameraSpec.builder()
                .mainMp(createDTO.getMainCameraMp())
                .ultrawideMp(createDTO.getUltrawideCameraMp())
                .telephotoMp(createDTO.getTelephotoMp())
                .selfieMp(createDTO.getSelfieCameraMp())
                .video4k(createDTO.getVideo4k())
                .video8k(createDTO.getVideo8k())
                .build();

        // Build battery spec
        BatterySpec batterySpec = BatterySpec.builder()
                .capacityMah(createDTO.getBatteryCapacityMah())
                .chargeWiredW(createDTO.getChargeWiredW())
                .chargeWirelessW(createDTO.getChargeWirelessW())
                .reverseChargeW(createDTO.getReverseChargeW())
                .build();

        // Build connectivity spec
        ConnectivitySpec connectivitySpec = ConnectivitySpec.builder()
                .wifiVersion(createDTO.getWifiVersion())
                .bluetoothVersion(createDTO.getBluetoothVersion())
                .nfc(createDTO.getNfc())
                .usbType(createDTO.getUsbType())
                .gpsSystems(createDTO.getGpsSystems())
                .cellular5g(createDTO.getCellular5g())
                .uwb(createDTO.getUwb())
                .irBlaster(createDTO.getIrBlaster())
                .build();

        return Model.builder()
                .name(createDTO.getName())
                .releaseYear(createDTO.getReleaseYear())
                .os(createDTO.getOs())
                .chipset(createDTO.getChipset())
                .cpuCores(createDTO.getCpuCores())
                .cpuMaxGhz(createDTO.getCpuMaxGhz())
                .gpu(createDTO.getGpu())
                .display(displaySpec)
                .camera(cameraSpec)
                .battery(batterySpec)
                .connectivity(connectivitySpec)
                .specsJson(createDTO.getSpecsJson() != null ? createDTO.getSpecsJson() : new HashMap<>())
                .isActive(createDTO.getIsActive() != null ? createDTO.getIsActive() : true)
                .build();
    }

    /**
     * Chuyển đổi từ Model entity sang ModelResponseDTO
     */
    public ModelResponseDTO toResponseDTO(Model model) {
        if (model == null) {
            return null;
        }

        ModelResponseDTO.ModelResponseDTOBuilder builder = ModelResponseDTO.builder()
                .id(model.getId())
                .name(model.getName())
                .releaseYear(model.getReleaseYear())
                .os(model.getOs())
                .chipset(model.getChipset())
                .cpuCores(model.getCpuCores())
                .cpuMaxGhz(model.getCpuMaxGhz())
                .gpu(model.getGpu())
                .specsJson(model.getSpecsJson())
                .isActive(model.getIsActive())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt());

        // Map brand information
        if (model.getBrand() != null) {
            builder.brandId(model.getBrand().getId())
                   .brandName(model.getBrand().getName());
        }

        // Map category information
        if (model.getCategory() != null) {
            builder.categoryId(model.getCategory().getId())
                   .categoryName(model.getCategory().getName());
        }

        // Map display specs
        if (model.getDisplay() != null) {
            DisplaySpec display = model.getDisplay();
            builder.displaySizeInch(display.getSizeInch())
                   .displayResolution(display.getResolution())
                   .displayRefreshHz(display.getRefreshHz())
                   .displayPanel(display.getPanel())
                   .displayBrightnessNits(display.getBrightnessNits())
                   .glassProtection(display.getGlassProtection());
        }

        // Map camera specs
        if (model.getCamera() != null) {
            CameraSpec camera = model.getCamera();
            builder.mainCameraMp(camera.getMainMp())
                   .ultrawideCameraMp(camera.getUltrawideMp())
                   .telephotoMp(camera.getTelephotoMp())
                   .selfieCameraMp(camera.getSelfieMp())
                   .video4k(camera.getVideo4k())
                   .video8k(camera.getVideo8k());
        }

        // Map battery specs
        if (model.getBattery() != null) {
            BatterySpec battery = model.getBattery();
            builder.batteryCapacityMah(battery.getCapacityMah())
                   .chargeWiredW(battery.getChargeWiredW())
                   .chargeWirelessW(battery.getChargeWirelessW())
                   .reverseChargeW(battery.getReverseChargeW());
        }

        // Map connectivity specs
        if (model.getConnectivity() != null) {
            ConnectivitySpec connectivity = model.getConnectivity();
            builder.wifiVersion(connectivity.getWifiVersion())
                   .bluetoothVersion(connectivity.getBluetoothVersion())
                   .nfc(connectivity.getNfc())
                   .usbType(connectivity.getUsbType())
                   .gpsSystems(connectivity.getGpsSystems())
                   .cellular5g(connectivity.getCellular5g())
                   .uwb(connectivity.getUwb())
                   .irBlaster(connectivity.getIrBlaster());
        }

        return builder.build();
    }

    /**
     * Cập nhật Model entity từ ModelUpdateDTO
     */
    public void updateEntityFromDTO(Model model, ModelUpdateDTO updateDTO) {
        if (model == null || updateDTO == null) {
            return;
        }

        // Update basic fields
        if (updateDTO.getName() != null) {
            model.setName(updateDTO.getName());
        }
        if (updateDTO.getReleaseYear() != null) {
            model.setReleaseYear(updateDTO.getReleaseYear());
        }
        if (updateDTO.getOs() != null) {
            model.setOs(updateDTO.getOs());
        }
        if (updateDTO.getChipset() != null) {
            model.setChipset(updateDTO.getChipset());
        }
        if (updateDTO.getCpuCores() != null) {
            model.setCpuCores(updateDTO.getCpuCores());
        }
        if (updateDTO.getCpuMaxGhz() != null) {
            model.setCpuMaxGhz(updateDTO.getCpuMaxGhz());
        }
        if (updateDTO.getGpu() != null) {
            model.setGpu(updateDTO.getGpu());
        }

        // Update display specs
        updateDisplaySpecs(model, updateDTO);

        // Update camera specs
        updateCameraSpecs(model, updateDTO);

        // Update battery specs
        updateBatterySpecs(model, updateDTO);

        // Update connectivity specs
        updateConnectivitySpecs(model, updateDTO);

        if (updateDTO.getSpecsJson() != null) {
            model.setSpecsJson(updateDTO.getSpecsJson());
        }
        if (updateDTO.getIsActive() != null) {
            model.setIsActive(updateDTO.getIsActive());
        }
    }

    /**
     * Chuyển đổi danh sách Model entities sang danh sách ModelResponseDTO
     */
    public List<ModelResponseDTO> toResponseDTOList(List<Model> models) {
        if (models == null) {
            return null;
        }
        return models.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Tạo ModelResponseDTO tóm tắt (chỉ thông tin cơ bản)
     */
    public ModelResponseDTO toSummaryResponseDTO(Model model) {
        if (model == null) {
            return null;
        }

        return ModelResponseDTO.builder()
                .id(model.getId())
                .name(model.getName())
                .brandId(model.getBrand() != null ? model.getBrand().getId() : null)
                .brandName(model.getBrand() != null ? model.getBrand().getName() : null)
                .categoryId(model.getCategory() != null ? model.getCategory().getId() : null)
                .categoryName(model.getCategory() != null ? model.getCategory().getName() : null)
                .releaseYear(model.getReleaseYear())
                .os(model.getOs())
                .displaySizeInch(model.getDisplay() != null ? model.getDisplay().getSizeInch() : null)
                .batteryCapacityMah(model.getBattery() != null ? model.getBattery().getCapacityMah() : null)
                .mainCameraMp(model.getCamera() != null ? model.getCamera().getMainMp() : null)
                .isActive(model.getIsActive())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }

    // Private helper methods for updating embedded objects

    private void updateDisplaySpecs(Model model, ModelUpdateDTO updateDTO) {
        DisplaySpec display = model.getDisplay();
        if (display == null) {
            display = new DisplaySpec();
            model.setDisplay(display);
        }

        if (updateDTO.getDisplaySizeInch() != null) {
            display.setSizeInch(updateDTO.getDisplaySizeInch());
        }
        if (updateDTO.getDisplayResolution() != null) {
            display.setResolution(updateDTO.getDisplayResolution());
        }
        if (updateDTO.getDisplayRefreshHz() != null) {
            display.setRefreshHz(updateDTO.getDisplayRefreshHz());
        }
        if (updateDTO.getDisplayPanel() != null) {
            display.setPanel(updateDTO.getDisplayPanel());
        }
        if (updateDTO.getDisplayBrightnessNits() != null) {
            display.setBrightnessNits(updateDTO.getDisplayBrightnessNits());
        }
        if (updateDTO.getGlassProtection() != null) {
            display.setGlassProtection(updateDTO.getGlassProtection());
        }
    }

    private void updateCameraSpecs(Model model, ModelUpdateDTO updateDTO) {
        CameraSpec camera = model.getCamera();
        if (camera == null) {
            camera = new CameraSpec();
            model.setCamera(camera);
        }

        if (updateDTO.getMainCameraMp() != null) {
            camera.setMainMp(updateDTO.getMainCameraMp());
        }
        if (updateDTO.getUltrawideCameraMp() != null) {
            camera.setUltrawideMp(updateDTO.getUltrawideCameraMp());
        }
        if (updateDTO.getTelephotoMp() != null) {
            camera.setTelephotoMp(updateDTO.getTelephotoMp());
        }
        if (updateDTO.getSelfieCameraMp() != null) {
            camera.setSelfieMp(updateDTO.getSelfieCameraMp());
        }
        if (updateDTO.getVideo4k() != null) {
            camera.setVideo4k(updateDTO.getVideo4k());
        }
        if (updateDTO.getVideo8k() != null) {
            camera.setVideo8k(updateDTO.getVideo8k());
        }
    }

    private void updateBatterySpecs(Model model, ModelUpdateDTO updateDTO) {
        BatterySpec battery = model.getBattery();
        if (battery == null) {
            battery = new BatterySpec();
            model.setBattery(battery);
        }

        if (updateDTO.getBatteryCapacityMah() != null) {
            battery.setCapacityMah(updateDTO.getBatteryCapacityMah());
        }
        if (updateDTO.getChargeWiredW() != null) {
            battery.setChargeWiredW(updateDTO.getChargeWiredW());
        }
        if (updateDTO.getChargeWirelessW() != null) {
            battery.setChargeWirelessW(updateDTO.getChargeWirelessW());
        }
        if (updateDTO.getReverseChargeW() != null) {
            battery.setReverseChargeW(updateDTO.getReverseChargeW());
        }
    }

    private void updateConnectivitySpecs(Model model, ModelUpdateDTO updateDTO) {
        ConnectivitySpec connectivity = model.getConnectivity();
        if (connectivity == null) {
            connectivity = new ConnectivitySpec();
            model.setConnectivity(connectivity);
        }

        if (updateDTO.getWifiVersion() != null) {
            connectivity.setWifiVersion(updateDTO.getWifiVersion());
        }
        if (updateDTO.getBluetoothVersion() != null) {
            connectivity.setBluetoothVersion(updateDTO.getBluetoothVersion());
        }
        if (updateDTO.getNfc() != null) {
            connectivity.setNfc(updateDTO.getNfc());
        }
        if (updateDTO.getUsbType() != null) {
            connectivity.setUsbType(updateDTO.getUsbType());
        }
        if (updateDTO.getGpsSystems() != null) {
            connectivity.setGpsSystems(updateDTO.getGpsSystems());
        }
        if (updateDTO.getCellular5g() != null) {
            connectivity.setCellular5g(updateDTO.getCellular5g());
        }
        if (updateDTO.getUwb() != null) {
            connectivity.setUwb(updateDTO.getUwb());
        }
        if (updateDTO.getIrBlaster() != null) {
            connectivity.setIrBlaster(updateDTO.getIrBlaster());
        }
    }
}
