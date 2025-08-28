import React, { useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import "./WarrantyLookup.css";



const formatVND = (n) =>
  n.toLocaleString("vi-VN", { style: "currency", currency: "VND", maximumFractionDigits: 0 });

const WarrantyLookup = () => {
  const [imei, setImei] = useState("");
  const [phone, setPhone] = useState("");
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);   // {ok, data|error}
  const [touched, setTouched] = useState(false);
    const navigate = useNavigate();

  const imeiHint = useMemo(() => {
    if (!touched) return "";
    if (!imei) return "";
    if (imei.length < 14) return "IMEI thường 14–16 ký tự.";
    return "";
  }, [imei, touched]);


  const onSubmit = async (e) => {
    e.preventDefault();
    setTouched(true);
    setLoading(true);
    setResult(null);
    try {
      const res = await axios.post("/api/warranty-lookup", {
        imei: imei.trim(),
        phone: phone.trim(),
      });
      setResult({ ok: true, data: res.data });
    } catch (err) {
      let msg = "Lỗi kết nối máy chủ.";
      if (err.response && err.response.data && err.response.data.error) {
        msg = err.response.data.error;
      }
      setResult({ ok: false, error: msg });
    }
    setLoading(false);
  };

  const onClear = () => {
    setImei(""); setPhone(""); setResult(null); setTouched(false);
  };

    const handleScanQR = () => {
      navigate("/qr-scan");
    };

    return (
    <div className="wl">
      <div className="wl__head">
        <h1 className="wl__title">Tra cứu bảo hành</h1>
        <div className="wl__actions">
           <button className="btn btn--green" onClick={handleScanQR}>
  Quét QR / Barcode
</button>
        </div>
      </div>

      {/* Form tra cứu */}
      <form className="card wl__form" onSubmit={onSubmit}>
        <div className="wl__form-grid">
          <div className="form-group">
            <label>IMEI / Serial</label>
            <input
              value={imei}
              onChange={(e)=>setImei(e.target.value)}
              onBlur={()=>setTouched(true)}
              placeholder="Nhập IMEI (14–16 ký tự)"
              className={imeiHint ? "is-invalid" : ""}
            />
            {imeiHint && <div className="hint error">{imeiHint}</div>}
          </div>

          <div className="form-group">
            <label>Số điện thoại khách hàng (tuỳ chọn)</label>
            <input
              value={phone}
              onChange={(e)=>setPhone(e.target.value)}
              placeholder="VD: 0912xxxxxx"
            />
            <div className="hint">Có thể dùng SĐT để tra nhanh lịch sử thiết bị.</div>
          </div>

          <div className="form-actions">
            <button type="submit" className="btn btn--primary" disabled={loading}>
              {loading ? "Đang tra..." : "Tra cứu"}
            </button>
            <button type="button" className="btn" onClick={onClear} disabled={loading}>Làm mới</button>
          </div>
        </div>
      </form>

      {/* Kết quả */}
      <div className="wl__result">
        {!result && <EmptyState />}

        {result && !result.ok && (
          <div className="card empty error">
            <div className="empty__icon">⚠️</div>
            <div className="empty__title">{result.error}</div>
            <div className="empty__desc">Kiểm tra lại IMEI/SĐT hoặc thử quét QR/Barcode.</div>
          </div>
        )}

        {result && result.ok && (
          <div className="wl__grid">
            <InfoCard data={result.data} />
            <Timeline history={result.data.warranty.history} />
          </div>
        )}
      </div>
    </div>
  );
};

/** ====== Presentational components ====== */
const Badge = ({ kind = "default", children }) => {
  const cls = `badge ${kind === "success" ? "badge--success" : kind === "danger" ? "badge--danger" : kind === "warning" ? "badge--warning" : ""}`;
  return <span className={cls}>{children}</span>;
};

const EmptyState = () => (
  <div className="card empty">
    <div className="empty__icon">🔎</div>
    <div className="empty__title">Nhập IMEI hoặc SĐT để tra bảo hành</div>
    <div className="empty__desc">Hỗ trợ quét QR/Barcode để điền nhanh.</div>
  </div>
);

const Row = ({ label, children }) => (
  <div className="row">
    <div className="row__label">{label}</div>
    <div className="row__value">{children}</div>
  </div>
);

const InfoCard = ({ data }) => {
  const { customer, device, order, warranty } = data;

  const now = new Date();
  const end = new Date(warranty.endDate);
  const isExpired = end < now;
  const badgeKind = warranty.status === "Đang xử lý" ? "warning" : isExpired ? "danger" : "success";

  return (
    <div className="card">
      <div className="card__head">
        <h3 className="card__title">Thông tin bảo hành</h3>
        <Badge kind={badgeKind}>{isExpired ? "Hết hạn" : warranty.status}</Badge>
      </div>

      <div className="card__section">
        <h4 className="section__title">Khách hàng</h4>
        <Row label="Họ tên">{customer.name}</Row>
        <Row label="SĐT">{customer.phone}</Row>
      </div>

      <div className="card__section">
        <h4 className="section__title">Thiết bị</h4>
        <Row label="Sản phẩm">{device.productName}</Row>
        <Row label="IMEI">{device.imei}</Row>
        <Row label="Màu/Model">{device.color} • {device.sku}</Row>
      </div>

      <div className="card__section">
        <h4 className="section__title">Đơn hàng</h4>
        <Row label="Mã đơn">{order.code}</Row>
        <Row label="Ngày mua">{order.date}</Row>
        <Row label="Giá trị">{formatVND(order.price)}</Row>
      </div>

      <div className="card__section">
        <h4 className="section__title">Bảo hành</h4>
        <Row label="Bắt đầu">{warranty.startDate}</Row>
        <Row label="Kết thúc">{warranty.endDate}</Row>
        <Row label="Trạng thái">
          <Badge kind={badgeKind}>{isExpired ? "Hết hạn" : warranty.status}</Badge>
        </Row>
      </div>

      <div className="card__footer">
        <button className="btn btn--green" onClick={()=>alert("TODO: tạo phiếu tiếp nhận")}>Tiếp nhận bảo hành</button>
        <button className="btn" onClick={()=>alert("TODO: đánh dấu hoàn tất")}>Hoàn tất / Trả máy</button>
      </div>
    </div>
  );
};

const Timeline = ({ history = [] }) => (
  <div className="card">
    <div className="card__head">
      <h3 className="card__title">Lịch sử xử lý</h3>
    </div>
    <ul className="timeline">
      {history.length === 0 && <li className="timeline__empty">Chưa có lịch sử bảo hành.</li>}
      {history.map((h, i) => (
        <li key={i} className="timeline__item">
          <div className="dot" />
          <div className="content">
            <div className="time">{h.at}</div>
            <div className="title">{h.title}</div>
            {h.note && <div className="note">{h.note}</div>}
          </div>
        </li>
      ))}
    </ul>
  </div>
);


export default WarrantyLookup;
