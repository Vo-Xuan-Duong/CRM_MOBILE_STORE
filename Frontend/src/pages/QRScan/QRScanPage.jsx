import React, { useState, useRef, useEffect } from 'react';
import { Camera, X, Flashlight, RotateCcw, CheckCircle, AlertCircle } from 'lucide-react';

const QRScanPage = () => {
  const [isScanning, setIsScanning] = useState(false);
  const [scannedData, setScannedData] = useState(null);
  const [error, setError] = useState(null);
  const [hasPermission, setHasPermission] = useState(null);
  const [torchEnabled, setTorchEnabled] = useState(false);
  const videoRef = useRef(null);
  const canvasRef = useRef(null);
  const streamRef = useRef(null);
  const scanIntervalRef = useRef(null);

  // Request camera permission
  const requestCameraPermission = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ 
        video: { facingMode: 'environment' } 
      });
      setHasPermission(true);
      streamRef.current = stream;
      if (videoRef.current) {
        videoRef.current.srcObject = stream;
      }
      return stream;
    } catch (err) {
      setHasPermission(false);
      setError('Không thể truy cập camera. Vui lòng cấp quyền camera.');
      return null;
    }
  };

  // Stop camera stream
  const stopCamera = () => {
    if (streamRef.current) {
      streamRef.current.getTracks().forEach(track => track.stop());
      streamRef.current = null;
    }
    if (scanIntervalRef.current) {
      clearInterval(scanIntervalRef.current);
      scanIntervalRef.current = null;
    }
  };

  // Mock QR code detection (in real app, use a QR scanning library like qr-scanner)
  const detectQRCode = () => {
    // This is a simplified mock - in reality you'd use a proper QR scanning library
    const mockQRData = [
      'https://example.com',
      '{"name": "Nguyễn Văn A", "phone": "0123456789"}',
      'WIFI:T:WPA;S:MyNetwork;P:password123;;',
      'Xin chào! Đây là mã QR test.'
    ];
    
    // Simulate random QR detection
    if (Math.random() > 0.95) {
      const randomData = mockQRData[Math.floor(Math.random() * mockQRData.length)];
      setScannedData(randomData);
      setIsScanning(false);
      stopCamera();
    }
  };

  // Start scanning
  const startScanning = async () => {
    setError(null);
    setScannedData(null);
    
    if (hasPermission === null) {
      await requestCameraPermission();
    }
    
    if (hasPermission !== false) {
      setIsScanning(true);
      
      // Start QR detection interval
      scanIntervalRef.current = setInterval(detectQRCode, 100);
    }
  };

  // Stop scanning
  const stopScanning = () => {
    setIsScanning(false);
    stopCamera();
  };

  // Toggle torch/flashlight
  const toggleTorch = async () => {
    if (streamRef.current) {
      const track = streamRef.current.getVideoTracks()[0];
      if (track && track.getCapabilities().torch) {
        try {
          await track.applyConstraints({
            advanced: [{ torch: !torchEnabled }]
          });
          setTorchEnabled(!torchEnabled);
        } catch (err) {
          console.error('Không thể bật/tắt đèn flash');
        }
      }
    }
  };

  // Reset scanner
  const resetScanner = () => {
    setScannedData(null);
    setError(null);
    setIsScanning(false);
    stopCamera();
  };

  // Cleanup on unmount
  useEffect(() => {
    return () => {
      stopCamera();
    };
  }, []);

  // Format scanned data for display
  const formatScannedData = (data) => {
    try {
      // Try to parse as JSON
      const parsed = JSON.parse(data);
      return (
        <div className="space-y-2">
          <p className="font-medium text-gray-800">Dữ liệu JSON:</p>
          <pre className="bg-gray-100 p-3 rounded text-sm overflow-x-auto">
            {JSON.stringify(parsed, null, 2)}
          </pre>
        </div>
      );
    } catch {
      // Check if it's a URL
      if (data.startsWith('http://') || data.startsWith('https://')) {
        return (
          <div className="space-y-2">
            <p className="font-medium text-gray-800">Liên kết:</p>
            <a 
              href={data} 
              target="_blank" 
              rel="noopener noreferrer"
              className="text-blue-600 hover:text-blue-800 underline break-all"
            >
              {data}
            </a>
          </div>
        );
      }
      
      // Check if it's WiFi data
      if (data.startsWith('WIFI:')) {
        const wifiData = data.match(/WIFI:T:(.*?);S:(.*?);P:(.*?);;/);
        if (wifiData) {
          return (
            <div className="space-y-2">
              <p className="font-medium text-gray-800">Thông tin WiFi:</p>
              <div className="bg-gray-100 p-3 rounded">
                <p><strong>Tên mạng:</strong> {wifiData[2]}</p>
                <p><strong>Loại bảo mật:</strong> {wifiData[1]}</p>
                <p><strong>Mật khẩu:</strong> {wifiData[3]}</p>
              </div>
            </div>
          );
        }
      }
      
      // Default text display
      return (
        <div className="space-y-2">
          <p className="font-medium text-gray-800">Văn bản:</p>
          <p className="bg-gray-100 p-3 rounded break-all">{data}</p>
        </div>
      );
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
      <div className="container mx-auto px-4 py-6 max-w-md">
        {/* Header */}
        <div className="text-center mb-6">
          <h1 className="text-2xl font-bold text-gray-800 mb-2">
            Quét Mã QR
          </h1>
          <p className="text-gray-600">
            Hướng camera vào mã QR để quét
          </p>
        </div>

        {/* Scanner Area */}
        <div className="bg-white rounded-2xl shadow-lg overflow-hidden mb-6">
          {!isScanning && !scannedData && !error && (
            <div className="aspect-square flex flex-col items-center justify-center p-8 bg-gray-50">
              <Camera className="w-16 h-16 text-gray-400 mb-4" />
              <p className="text-gray-600 text-center mb-6">
                Nhấn nút bên dưới để bắt đầu quét mã QR
              </p>
            </div>
          )}

          {isScanning && (
            <div className="relative aspect-square bg-black">
              <video
                ref={videoRef}
                autoPlay
                playsInline
                muted
                className="w-full h-full object-cover"
              />
              <canvas
                ref={canvasRef}
                className="hidden"
              />
              
              {/* Scanning overlay */}
              <div className="absolute inset-0 flex items-center justify-center">
                <div className="relative w-64 h-64">
                  <div className="absolute inset-0 border-2 border-white opacity-50 rounded-lg"></div>
                  <div className="absolute top-0 left-0 w-6 h-6 border-t-4 border-l-4 border-blue-500 rounded-tl-lg"></div>
                  <div className="absolute top-0 right-0 w-6 h-6 border-t-4 border-r-4 border-blue-500 rounded-tr-lg"></div>
                  <div className="absolute bottom-0 left-0 w-6 h-6 border-b-4 border-l-4 border-blue-500 rounded-bl-lg"></div>
                  <div className="absolute bottom-0 right-0 w-6 h-6 border-b-4 border-r-4 border-blue-500 rounded-br-lg"></div>
                  
                  {/* Scanning line animation */}
                  <div className="absolute inset-x-0 top-0 h-1 bg-blue-500 animate-pulse"></div>
                </div>
              </div>

              {/* Flash button */}
              <button
                onClick={toggleTorch}
                className={`absolute top-4 right-4 p-3 rounded-full ${
                  torchEnabled ? 'bg-yellow-500 text-white' : 'bg-black bg-opacity-50 text-white'
                } hover:bg-opacity-70 transition-colors`}
              >
                <Flashlight className="w-5 h-5" />
              </button>

              {/* Stop button */}
              <button
                onClick={stopScanning}
                className="absolute top-4 left-4 p-3 rounded-full bg-black bg-opacity-50 text-white hover:bg-opacity-70 transition-colors"
              >
                <X className="w-5 h-5" />
              </button>
            </div>
          )}

          {scannedData && (
            <div className="p-6">
              <div className="flex items-center mb-4">
                <CheckCircle className="w-6 h-6 text-green-500 mr-2" />
                <h3 className="text-lg font-semibold text-gray-800">
                  Quét thành công!
                </h3>
              </div>
              <div className="bg-gray-50 rounded-lg p-4">
                {formatScannedData(scannedData)}
              </div>
            </div>
          )}

          {error && (
            <div className="p-6">
              <div className="flex items-center mb-4">
                <AlertCircle className="w-6 h-6 text-red-500 mr-2" />
                <h3 className="text-lg font-semibold text-gray-800">
                  Có lỗi xảy ra
                </h3>
              </div>
              <p className="text-gray-600 bg-red-50 p-4 rounded-lg">
                {error}
              </p>
            </div>
          )}
        </div>

        {/* Control Buttons */}
        <div className="space-y-3">
          {!isScanning && !scannedData && (
            <button
              onClick={startScanning}
              className="w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-4 px-6 rounded-xl transition-colors flex items-center justify-center"
            >
              <Camera className="w-5 h-5 mr-2" />
              Bắt đầu quét
            </button>
          )}

          {isScanning && (
            <button
              onClick={stopScanning}
              className="w-full bg-red-600 hover:bg-red-700 text-white font-semibold py-4 px-6 rounded-xl transition-colors flex items-center justify-center"
            >
              <X className="w-5 h-5 mr-2" />
              Dừng quét
            </button>
          )}

          {(scannedData || error) && (
            <button
              onClick={resetScanner}
              className="w-full bg-gray-600 hover:bg-gray-700 text-white font-semibold py-4 px-6 rounded-xl transition-colors flex items-center justify-center"
            >
              <RotateCcw className="w-5 h-5 mr-2" />
              Quét lại
            </button>
          )}
        </div>

        {/* Instructions */}
        <div className="mt-8 bg-white rounded-xl p-6 shadow-lg">
          <h3 className="font-semibold text-gray-800 mb-3">Hướng dẫn sử dụng:</h3>
          <ul className="space-y-2 text-gray-600">
            <li className="flex items-start">
              <span className="w-6 h-6 bg-blue-100 text-blue-600 rounded-full flex items-center justify-center text-sm font-medium mr-3 mt-0.5 flex-shrink-0">1</span>
              Nhấn nút "Bắt đầu quét" để mở camera
            </li>
            <li className="flex items-start">
              <span className="w-6 h-6 bg-blue-100 text-blue-600 rounded-full flex items-center justify-center text-sm font-medium mr-3 mt-0.5 flex-shrink-0">2</span>
              Hướng camera vào mã QR trong khung vuông
            </li>
            <li className="flex items-start">
              <span className="w-6 h-6 bg-blue-100 text-blue-600 rounded-full flex items-center justify-center text-sm font-medium mr-3 mt-0.5 flex-shrink-0">3</span>
              Đợi hệ thống tự động nhận diện và hiển thị kết quả
            </li>
            <li className="flex items-start">
              <span className="w-6 h-6 bg-blue-100 text-blue-600 rounded-full flex items-center justify-center text-sm font-medium mr-3 mt-0.5 flex-shrink-0">4</span>
              Sử dụng nút đèn flash nếu cần thêm ánh sáng
            </li>
          </ul>
        </div>
      </div>
    </div>
  );
};

export default QRScanPage;