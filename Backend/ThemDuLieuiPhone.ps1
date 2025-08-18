# Script PowerShell để thêm dữ liệu iPhone 15 Pro Max vào hệ thống
Write-Host "Đang thêm dữ liệu iPhone 15 Pro Max vào hệ thống CRM Mobile Store..." -ForegroundColor Green

$baseUrl = "http://localhost:8080/api"

# Hàm gọi API với xử lý lỗi
function Invoke-APICall {
    param(
        [string]$Url,
        [hashtable]$Body,
        [string]$Method = "POST"
    )

    try {
        $headers = @{
            "Content-Type" = "application/json"
        }

        $jsonBody = $Body | ConvertTo-Json -Depth 10
        $response = Invoke-RestMethod -Uri $Url -Method $Method -Body $jsonBody -Headers $headers
        return $response
    }
    catch {
        Write-Host "Lỗi khi gọi API $Url`: $($_.Exception.Message)" -ForegroundColor Red
        return $null
    }
}

Write-Host "`nBước 1: Tạo thương hiệu Apple..." -ForegroundColor Yellow
$brandData = @{
    name = "Apple"
    country = "United States"
    website = "https://www.apple.com"
}

$brandResponse = Invoke-APICall -Url "$baseUrl/brands" -Body $brandData
if ($brandResponse) {
    $brandId = $brandResponse.data.id
    Write-Host "✓ Đã tạo thương hiệu Apple với ID: $brandId" -ForegroundColor Green
} else {
    Write-Host "✗ Không thể tạo thương hiệu Apple" -ForegroundColor Red
    exit 1
}

Write-Host "`nBước 2: Tạo danh mục Smartphone..." -ForegroundColor Yellow
$categoryData = @{
    name = "Smartphone"
    parentId = $null
}

$categoryResponse = Invoke-APICall -Url "$baseUrl/categories" -Body $categoryData
if ($categoryResponse) {
    $categoryId = $categoryResponse.data.id
    Write-Host "✓ Đã tạo danh mục Smartphone với ID: $categoryId" -ForegroundColor Green
} else {
    Write-Host "✗ Không thể tạo danh mục Smartphone" -ForegroundColor Red
    exit 1
}

Write-Host "`nBước 3: Tạo model iPhone 15 Pro Max..." -ForegroundColor Yellow
$modelData = @{
    brandId = $brandId
    categoryId = $categoryId
    name = "iPhone 15 Pro Max 256GB"
    releaseYear = 2023
    os = "IOS"
    chipset = "Apple A17 Pro"
    cpuCores = 6
    cpuMaxGhz = 3.78
    gpu = "Apple GPU (6-core graphics)"
    displaySizeInch = 6.7
    displayResolution = "1290x2796"
    displayRefreshHz = 120
    displayPanel = "Super Retina XDR OLED"
    displayBrightnessNits = 2000
    glassProtection = "Ceramic Shield"
    mainCameraMp = 48
    ultrawideCameraMp = 12
    telephotoMp = 12
    selfieCameraMp = 12
    video4k = $true
    video8k = $false
    batteryCapacityMah = 4422
    chargeWiredW = 20
    chargeWirelessW = 15
    wifiVersion = "Wi-Fi 6E"
    bluetoothVersion = "5.3"
    nfc = $true
    usbType = "Lightning"
    gpsSystems = "GPS, GLONASS, Galileo, QZSS, BeiDou"
    cellular5g = $true
    uwb = $true
    irBlaster = $false
    isActive = $true
}

$modelResponse = Invoke-APICall -Url "$baseUrl/models" -Body $modelData
if ($modelResponse) {
    $modelId = $modelResponse.data.id
    Write-Host "✓ Đã tạo model iPhone 15 Pro Max với ID: $modelId" -ForegroundColor Green
} else {
    Write-Host "✗ Không thể tạo model iPhone 15 Pro Max" -ForegroundColor Red
    exit 1
}

Write-Host "`nBước 4: Tạo sản phẩm..." -ForegroundColor Yellow
$productData = @{
    sku = "IPHONE15PROMAX256GB"
    name = "iPhone 15 Pro Max 256GB"
    brandId = $brandId
    categoryId = $categoryId
    modelId = $modelId
    unitPrice = 30390000
    isActive = $true
    attributes = @{
        color = "Blue Titanium"
        storage = "256GB"
        ram = "8GB"
        originalPrice = 30390000
        salePrice = 29390000
        discount = "3%"
        rating = 4.9
        soldCount = "243.4k"
        imageUrl = "https://cdn.tgdd.vn/Products/Images/42/305658/iphone-15-pro-max-blue-thumbnew-600x600.jpg"
        productUrl = "https://www.thegioididong.com/dtdd/iphone-15-pro-max"
    }
}

$productResponse = Invoke-APICall -Url "$baseUrl/products" -Body $productData
if ($productResponse) {
    $productId = $productResponse.data.id
    Write-Host "✓ Đã tạo sản phẩm iPhone 15 Pro Max với ID: $productId" -ForegroundColor Green
} else {
    Write-Host "✗ Không thể tạo sản phẩm iPhone 15 Pro Max" -ForegroundColor Red
    exit 1
}

Write-Host "`nBước 5: Tạo biến thể sản phẩm..." -ForegroundColor Yellow
$variantData = @{
    modelId = $modelId
    color = "Blue Titanium"
    ramGb = 8
    storageGb = 256
    sku = "IPHONE15PROMAX256GB-BLUE"
    isActive = $true
}

$variantResponse = Invoke-APICall -Url "$baseUrl/variants" -Body $variantData
if ($variantResponse) {
    $variantId = $variantResponse.data.id
    Write-Host "✓ Đã tạo biến thể sản phẩm với ID: $variantId" -ForegroundColor Green
} else {
    Write-Host "✗ Không thể tạo biến thể sản phẩm" -ForegroundColor Red
}

Write-Host "`n🎉 Hoàn thành việc thêm dữ liệu iPhone 15 Pro Max vào hệ thống!" -ForegroundColor Green
Write-Host "📊 Tóm tắt:" -ForegroundColor Cyan
Write-Host "   - Thương hiệu ID: $brandId" -ForegroundColor White
Write-Host "   - Danh mục ID: $categoryId" -ForegroundColor White
Write-Host "   - Model ID: $modelId" -ForegroundColor White
Write-Host "   - Sản phẩm ID: $productId" -ForegroundColor White
Write-Host "   - Biến thể ID: $variantId" -ForegroundColor White

Write-Host "`n📱 Thông tin sản phẩm đã thêm:" -ForegroundColor Cyan
Write-Host "   - Tên: iPhone 15 Pro Max 256GB" -ForegroundColor White
Write-Host "   - Màu: Blue Titanium" -ForegroundColor White
Write-Host "   - Dung lượng: 256GB" -ForegroundColor White
Write-Host "   - RAM: 8GB" -ForegroundColor White
Write-Host "   - Giá bán: 29.390.000₫" -ForegroundColor White
Write-Host "   - Đánh giá: 4.9/5 sao" -ForegroundColor White
Write-Host "   - Đã bán: 243.4k" -ForegroundColor White
