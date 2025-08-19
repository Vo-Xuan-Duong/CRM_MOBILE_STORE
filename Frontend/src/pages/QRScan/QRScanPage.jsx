import React, { useState, useRef, useEffect } from 'react';
import { Camera, X, Flashlight, RotateCcw, CheckCircle, AlertCircle } from 'lucide-react';
import './QRScanPage.css';

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
    <div className="qr-scan-page">
      <div className="qr-scan-container">
        <div className="qr-scan-header">
          <h1 className="qr-scan-title">Quét Mã QR</h1>
          <p className="qr-scan-subtitle">Hướng camera vào mã QR để quét</p>
        </div>

        <div className="qr-scan-content">
          {!isScanning && !scannedData && !error && (
            <>
              <div className="qr-scan-icon">
                📷
              </div>
              <p className="qr-scan-instruction">
                Nhấn nút bên dưới để bắt đầu quét mã QR
              </p>
              <button
                onClick={startScanning}
                className="qr-scan-button"
              >
                <Camera />
                Bắt đầu quét
              </button>
            </>
          )}

          {isScanning && (
            <div className="qr-scanner-active">
              <video
                ref={videoRef}
                autoPlay
                playsInline
                muted
                className="qr-scanner-video"
              />
              <canvas
                ref={canvasRef}
                style={{ display: 'none' }}
              />

              <div className="qr-scanner-overlay">
                <div className="qr-scanner-frame">
                  <div className="qr-scanner-scan-line"></div>
                </div>
              </div>

              <div className="qr-scanner-controls">
                <button
                  onClick={stopScanning}
                  className="qr-scanner-btn"
                >
                  <X />
                </button>
                <button
                  onClick={toggleTorch}
                  className={`qr-scanner-btn flash ${torchEnabled ? 'active' : ''}`}
                >
                  <Flashlight />
                </button>
              </div>
            </div>
          )}

          {scannedData && (
            <div className="qr-scan-results success qr-scan-success">
              <h3>
                <CheckCircle />
                Quét thành công!
              </h3>
              <div className="qr-scan-data">
                {formatScannedData(scannedData)}
              </div>
              <button
                onClick={resetScanner}
                className="qr-scan-button"
                style={{ marginTop: '1rem' }}
              >
                <RotateCcw />
                Quét lại
              </button>
            </div>
          )}

          {error && (
            <div className="qr-scan-results error">
              <h3>
                <AlertCircle />
                Có lỗi xảy ra
              </h3>
              <div className="qr-scan-data">
                {error}
              </div>
              <button
                onClick={resetScanner}
                className="qr-scan-button"
                style={{ marginTop: '1rem' }}
              >
                <RotateCcw />
                Thử lại
              </button>
            </div>
          )}

          <div className="qr-scan-instructions">
            <h3>Hướng dẫn sử dụng:</h3>
            <ul>
              <li>
                <span className="step-number">1</span>
                <span className="step-text">Nhấn nút "Bắt đầu quét" để mở camera</span>
              </li>
              <li>
                <span className="step-number">2</span>
                <span className="step-text">Hướng camera vào mã QR trong khung vuông</span>
              </li>
              <li>
                <span className="step-number">3</span>
                <span className="step-text">Đợi hệ thống tự động nhận diện và hiển thị kết quả</span>
              </li>
              <li>
                <span className="step-number">4</span>
                <span className="step-text">Sử dụng nút đèn flash nếu cần thêm ánh sáng</span>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
};

export default QRScanPage;