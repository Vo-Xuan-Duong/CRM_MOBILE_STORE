@echo off
echo Adding iPhone 15 Pro Max data to CRM Mobile Store system...
echo.

set BASE_URL=http://localhost:8080/api

echo Step 1: Creating Apple brand...
curl -X POST %BASE_URL%/brands ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Apple\",\"country\":\"United States\",\"website\":\"https://www.apple.com\"}" ^
  > brand_response.json

echo.
echo Step 2: Creating Smartphone category...
curl -X POST %BASE_URL%/categories ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Smartphone\",\"parentId\":null}" ^
  > category_response.json

echo.
echo Step 3: Creating iPhone 15 Pro Max model...
curl -X POST %BASE_URL%/models ^
  -H "Content-Type: application/json" ^
  -d "{\"brandId\":1,\"categoryId\":1,\"name\":\"iPhone 15 Pro Max 256GB\",\"releaseYear\":2023,\"os\":\"IOS\",\"chipset\":\"Apple A17 Pro\",\"cpuCores\":6,\"cpuMaxGhz\":3.78,\"gpu\":\"Apple GPU (6-core graphics)\",\"displaySizeInch\":6.7,\"displayResolution\":\"1290x2796\",\"displayRefreshHz\":120,\"displayPanel\":\"Super Retina XDR OLED\",\"displayBrightnessNits\":2000,\"glassProtection\":\"Ceramic Shield\",\"mainCameraMp\":48,\"ultrawideCameraMp\":12,\"telephotoMp\":12,\"selfieCameraMp\":12,\"video4k\":true,\"video8k\":false,\"batteryCapacityMah\":4422,\"chargeWiredW\":20,\"chargeWirelessW\":15,\"wifiVersion\":\"Wi-Fi 6E\",\"bluetoothVersion\":\"5.3\",\"nfc\":true,\"usbType\":\"Lightning\",\"gpsSystems\":\"GPS, GLONASS, Galileo, QZSS, BeiDou\",\"cellular5g\":true,\"uwb\":true,\"irBlaster\":false,\"isActive\":true}" ^
  > model_response.json

echo.
echo Step 4: Creating product...
curl -X POST %BASE_URL%/products ^
  -H "Content-Type: application/json" ^
  -d "{\"sku\":\"IPHONE15PROMAX256GB\",\"name\":\"iPhone 15 Pro Max 256GB\",\"brandId\":1,\"categoryId\":1,\"modelId\":1,\"unitPrice\":30390000,\"isActive\":true,\"attributes\":{\"color\":\"Blue Titanium\",\"storage\":\"256GB\",\"ram\":\"8GB\",\"originalPrice\":30390000,\"salePrice\":29390000,\"discount\":\"3%\",\"rating\":4.9,\"soldCount\":\"243.4k\",\"imageUrl\":\"https://cdn.tgdd.vn/Products/Images/42/305658/iphone-15-pro-max-blue-thumbnew-600x600.jpg\",\"productUrl\":\"https://www.thegioididong.com/dtdd/iphone-15-pro-max\"}}" ^
  > product_response.json

echo.
echo Step 5: Creating variant...
curl -X POST %BASE_URL%/variants ^
  -H "Content-Type: application/json" ^
  -d "{\"modelId\":1,\"color\":\"Blue Titanium\",\"ramGb\":8,\"storageGb\":256,\"sku\":\"IPHONE15PROMAX256GB-BLUE\",\"isActive\":true}" ^
  > variant_response.json

echo.
echo Data insertion completed!
echo Check the response files for results:
echo - brand_response.json
echo - category_response.json
echo - model_response.json
echo - product_response.json
echo - variant_response.json

pause
