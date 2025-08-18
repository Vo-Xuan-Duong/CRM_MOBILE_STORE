#!/usr/bin/env python3
"""
Script to add iPhone 15 Pro Max data to the CRM Mobile Store system
"""

import requests
import json

# Base URL for the API
BASE_URL = "http://localhost:8080/api"

def get_auth_token():
    """Get authentication token (modify this based on your auth system)"""
    # You may need to authenticate first depending on your security config
    # For now, this is a placeholder
    return None

def create_brand_if_not_exists():
    """Create Apple brand if it doesn't exist"""
    brand_data = {
        "name": "Apple",
        "country": "United States",
        "website": "https://www.apple.com"
    }

    try:
        response = requests.post(f"{BASE_URL}/brands", json=brand_data)
        if response.status_code in [200, 201]:
            print("Apple brand created or already exists")
            return response.json().get('data', {}).get('id')
        else:
            print(f"Failed to create brand: {response.text}")
            return None
    except Exception as e:
        print(f"Error creating brand: {e}")
        return None

def create_category_if_not_exists():
    """Create Smartphone category if it doesn't exist"""
    category_data = {
        "name": "Smartphone",
        "parentId": None
    }

    try:
        response = requests.post(f"{BASE_URL}/categories", json=category_data)
        if response.status_code in [200, 201]:
            print("Smartphone category created or already exists")
            return response.json().get('data', {}).get('id')
        else:
            print(f"Failed to create category: {response.text}")
            return None
    except Exception as e:
        print(f"Error creating category: {e}")
        return None

def create_iphone_model(brand_id, category_id):
    """Create iPhone 15 Pro Max model"""
    model_data = {
        "brandId": brand_id,
        "categoryId": category_id,
        "name": "iPhone 15 Pro Max 256GB",
        "releaseYear": 2023,
        "os": "IOS",
        "chipset": "Apple A17 Pro",
        "cpuCores": 6,
        "cpuMaxGhz": 3.78,
        "gpu": "Apple GPU (6-core graphics)",

        # Display specifications
        "displaySizeInch": 6.7,
        "displayResolution": "1290x2796",
        "displayRefreshHz": 120,
        "displayPanel": "Super Retina XDR OLED",
        "displayBrightnessNits": 2000,
        "glassProtection": "Ceramic Shield",

        # Camera specifications
        "mainCameraMp": 48,
        "ultrawideCameraMp": 12,
        "telephotoMp": 12,
        "selfieCameraMp": 12,
        "video4k": True,
        "video8k": False,

        # Battery specifications
        "batteryCapacityMah": 4422,
        "chargeWiredW": 20,
        "chargeWirelessW": 15,
        "reverseChargeW": None,

        # Connectivity specifications
        "wifiVersion": "Wi-Fi 6E",
        "bluetoothVersion": "5.3",
        "nfc": True,
        "usbType": "Lightning",
        "gpsSystems": "GPS, GLONASS, Galileo, QZSS, BeiDou",
        "cellular5g": True,
        "uwb": True,
        "irBlaster": False,

        "isActive": True
    }

    try:
        response = requests.post(f"{BASE_URL}/models", json=model_data)
        if response.status_code in [200, 201]:
            print("iPhone 15 Pro Max model created successfully")
            return response.json().get('data', {}).get('id')
        else:
            print(f"Failed to create model: {response.text}")
            return None
    except Exception as e:
        print(f"Error creating model: {e}")
        return None

def create_product(model_id, brand_id, category_id):
    """Create the actual product entry"""
    product_data = {
        "sku": "IPHONE15PROMAX256GB",
        "name": "iPhone 15 Pro Max 256GB",
        "brandId": brand_id,
        "categoryId": category_id,
        "modelId": model_id,
        "unitPrice": 30390000,  # Price in VND (30.390.000₫)
        "isActive": True,
        "attributes": {
            "color": "Blue Titanium",
            "storage": "256GB",
            "ram": "8GB",
            "originalPrice": 30390000,
            "salePrice": 29390000,
            "discount": "3%",
            "rating": 4.9,
            "soldCount": "243.4k",
            "imageUrl": "https://cdn.tgdd.vn/Products/Images/42/305658/iphone-15-pro-max-blue-thumbnew-600x600.jpg",
            "productUrl": "https://www.thegioididong.com/dtdd/iphone-15-pro-max"
        }
    }

    try:
        response = requests.post(f"{BASE_URL}/products", json=product_data)
        if response.status_code in [200, 201]:
            print("iPhone 15 Pro Max product created successfully")
            return response.json().get('data', {}).get('id')
        else:
            print(f"Failed to create product: {response.text}")
            return None
    except Exception as e:
        print(f"Error creating product: {e}")
        return None

def main():
    """Main function to add iPhone 15 Pro Max data"""
    print("Starting iPhone 15 Pro Max data insertion...")

    # Step 1: Create or get Apple brand
    print("Step 1: Creating Apple brand...")
    brand_id = create_brand_if_not_exists()
    if not brand_id:
        print("Failed to create or get Apple brand. Exiting.")
        return

    # Step 2: Create or get Smartphone category
    print("Step 2: Creating Smartphone category...")
    category_id = create_category_if_not_exists()
    if not category_id:
        print("Failed to create or get Smartphone category. Exiting.")
        return

    # Step 3: Create iPhone 15 Pro Max model
    print("Step 3: Creating iPhone 15 Pro Max model...")
    model_id = create_iphone_model(brand_id, category_id)
    if not model_id:
        print("Failed to create iPhone 15 Pro Max model. Exiting.")
        return

    # Step 4: Create product
    print("Step 4: Creating iPhone 15 Pro Max product...")
    product_id = create_product(model_id, brand_id, category_id)
    if not product_id:
        print("Failed to create iPhone 15 Pro Max product. Exiting.")
        return

    print(f"Successfully added iPhone 15 Pro Max data!")
    print(f"Brand ID: {brand_id}")
    print(f"Category ID: {category_id}")
    print(f"Model ID: {model_id}")
    print(f"Product ID: {product_id}")

if __name__ == "__main__":
    main()
