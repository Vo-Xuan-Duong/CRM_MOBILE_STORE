import React, { useMemo, useRef, useState } from "react";
import "./Payment.css";

const VND = new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" });

const initialCart = [
  { id: 1, name: "iPhone 14 Pro 128GB", qty: 1, price: 21500000 },
  { id: 2, name: "Ốp lưng Magsafe", qty: 1, price: 450000 },
  { id: 3, name: "Cường lực Nillkin", qty: 2, price: 120000 },
];

export default function Payment() {
  // ------- Cart / Customer -------
  const [items, setItems] = useState(initialCart);
  const [customer, setCustomer] = useState({ name: "", phone: "", note: "" });

  // ------- Voucher -------
  const [voucher, setVoucher] = useState("");
  const [voucherApplied, setVoucherApplied] = useState(null); // { code, type, value }
  const [voucherError, setVoucherError] = useState("");

  // ------- Payment -------
  const [method1, setMethod1] = useState("cash"); // cash|card|qr|bank
  const [method2, setMethod2] = useState("card");
  const [split, setSplit] = useState(false);

  const [cash1, setCash1] = useState(0);     // số tiền khách đưa nếu PT1 = cash
  const [amount2, setAmount2] = useState(0); // số tiền PT2 khi tách

  const [cardInfo, setCardInfo] = useState({ number: "", name: "", expiry: "", cvv: "" });
  const [bankRef, setBankRef] = useState("");

  const [paid, setPaid] = useState(false);
  const receiptRef = useRef(null);

  // ------- Derived -------
  const subtotal = useMemo(() => items.reduce((s, it) => s + it.price * it.qty, 0), [items]);

  const discount = useMemo(() => {
    if (!voucherApplied) return 0;
    if (voucherApplied.type === "flat") return Math.min(subtotal, voucherApplied.value);
    if (voucherApplied.type === "percent") return Math.floor((subtotal * voucherApplied.value) / 100);
    return 0;
  }, [subtotal, voucherApplied]);

  const total = Math.max(0, subtotal - discount);

  const pm2Amount = split ? (+amount2 || 0) : 0;
  const pm1Amount = split
    ? (method1 === "cash" ? (+cash1 || 0) : Math.max(0, total - pm2Amount))
    : (method1 === "cash" ? (+cash1 || 0) : total);

  const received = pm1Amount + pm2Amount;
  const covered = split ? received >= total : (method1 === "cash" ? pm1Amount >= total : true);
  const change = Math.max(0, received - total);

  // ------- Handlers -------
  function handleQtyChange(id, nextQty) {
    setItems(prev => prev.map(it => (it.id === id ? { ...it, qty: Math.max(0, nextQty) } : it)).filter(it => it.qty > 0));
  }

  function applyVoucher() {
    const code = voucher.trim().toUpperCase();
    setVoucherError("");
    if (!code) return setVoucherApplied(null);

    if (code === "GIAM50K") setVoucherApplied({ code, type: "flat", value: 50000 });
    else if (code === "GIAM10") setVoucherApplied({ code, type: "percent", value: 10 });
    else if (code === "FREESHIP") setVoucherApplied({ code, type: "flat", value: 30000 });
    else {
      setVoucherApplied(null);
      setVoucherError("Mã không hợp lệ hoặc đã hết hiệu lực.");
    }
  }

  function formatCard(input) {
    return input.replace(/\D/g, "").slice(0, 16).replace(/(.{4})/g, "$1 ").trim();
  }

  function onPay() {
    // TODO: gọi API tạo hóa đơn tại đây nếu cần
    setPaid(true);
  }

  function printReceipt() {
    if (!receiptRef.current) return;
    const w = window.open("", "", "height=700,width=450");
    if (!w) return;
    w.document.write(`
      <html>
        <head>
          <title>Hóa đơn</title>
          <style>
            body { font-family: Arial, sans-serif; padding: 12px; }
            .center { text-align: center; }
            .muted { color: #666; font-size: 12px; }
            table { width: 100%; border-collapse: collapse; margin-top: 10px; }
            th, td { padding: 6px 0; border-bottom: 1px dashed #ddd; font-size: 14px; }
            .total { font-weight: 700; }
          </style>
        </head>
        <body>${receiptRef.current.innerHTML}</body>
      </html>
    `);
    w.document.close(); w.focus(); w.print(); w.close();
  }

  return (
    <div className="pay">
      <div className="pay__header">
        <h1>Thanh toán</h1>
        <div className="pay__header-actions">
          <button className="btn ghost" onClick={() => window.history.back()}>← Quay lại</button>
          <button className="btn" onClick={printReceipt} disabled={!paid}>In hóa đơn</button>
        </div>
      </div>

      <div className="pay__grid">
        {/* LEFT: Cart & Customer */}
        <section className="card">
          <h2>Giỏ hàng</h2>
          <div className="cart">
            {items.length === 0 ? (
              <div className="empty">Chưa có sản phẩm</div>
            ) : (
              items.map(it => (
                <div className="cart__row" key={it.id}>
                  <div className="cart__name">
                    <div className="txt-strong">{it.name}</div>
                    <div className="txt-muted">{VND.format(it.price)}</div>
                  </div>
                  <div className="cart__qty">
                    <button className="qtybtn" onClick={() => handleQtyChange(it.id, it.qty - 1)}>−</button>
                    <input type="number" min="1" value={it.qty}
                      onChange={e => handleQtyChange(it.id, parseInt(e.target.value || "0", 10))} />
                    <button className="qtybtn" onClick={() => handleQtyChange(it.id, it.qty + 1)}>+</button>
                  </div>
                  <div className="cart__line">{VND.format(it.qty * it.price)}</div>
                </div>
              ))
            )}
          </div>

          <div className="divider" />

          <div className="voucher">
            <input placeholder="Nhập mã (GIAM50K, GIAM10, FREESHIP)"
                   value={voucher} onChange={e => setVoucher(e.target.value)} />
            <button className="btn sm" onClick={applyVoucher}>Áp dụng</button>
          </div>
          {voucherApplied && (
            <div className="tag">
              Đã áp dụng mã <b>{voucherApplied.code}</b>
              <button className="link" onClick={() => setVoucherApplied(null)}>Hủy</button>
            </div>
          )}
          {!!voucherError && <div className="error">{voucherError}</div>}

          <div className="summary">
            <div className="row"><span>Tạm tính</span><b>{VND.format(subtotal)}</b></div>
            <div className="row"><span>Giảm giá</span><b className="txt-green">− {VND.format(discount)}</b></div>
            <div className="row total"><span>Khách cần trả</span><b>{VND.format(total)}</b></div>
          </div>

          <h2>Khách hàng</h2>
          <div className="form-grid">
            <label>Họ tên
              <input value={customer.name} onChange={e => setCustomer({ ...customer, name: e.target.value })} placeholder="Nguyễn Văn A" />
            </label>
            <label>SĐT
              <input value={customer.phone} onChange={e => setCustomer({ ...customer, phone: e.target.value })} placeholder="09xx xxx xxx" />
            </label>
            <label className="full">Ghi chú
              <textarea rows={2} value={customer.note} onChange={e => setCustomer({ ...customer, note: e.target.value })} placeholder="Giao trong hôm nay, gọi trước khi tới…" />
            </label>
          </div>
        </section>

        {/* RIGHT: Payment */}
        <section className="card">
          <h2>Phương thức thanh toán</h2>

          <div className="pm">
            <div className="pm__row">
              <label className="pm__label">PT 1</label>
              <select value={method1} onChange={e => setMethod1(e.target.value)}>
                <option value="cash">Tiền mặt</option>
                <option value="card">Thẻ (POS)</option>
                <option value="qr">QR Code</option>
                <option value="bank">Chuyển khoản</option>
              </select>

              {method1 === "cash" ? (
                <input type="number" min="0" step="1000" placeholder="Số tiền khách đưa"
                       value={cash1} onChange={e => setCash1(e.target.value)} />
              ) : (
                <div className="pm__hint">Sẽ thu đủ {VND.format(split ? Math.max(0, total - pm2Amount) : total)}</div>
              )}
            </div>

            <label className="split">
              <input type="checkbox" checked={split} onChange={e => setSplit(e.target.checked)} />
              Tách thanh toán (2 phương thức)
            </label>

            {split && (
              <div className="pm__row">
                <label className="pm__label">PT 2</label>
                <select value={method2} onChange={e => setMethod2(e.target.value)}>
                  <option value="cash">Tiền mặt</option>
                  <option value="card">Thẻ (POS)</option>
                  <option value="qr">QR Code</option>
                  <option value="bank">Chuyển khoản</option>
                </select>
                <input type="number" min="0" step="1000" placeholder="Số tiền PT2"
                       value={amount2} onChange={e => setAmount2(e.target.value)} />
              </div>
            )}

            {(method1 === "card" || (split && method2 === "card")) && (
              <div className="cardbox">
                <div className="grid2">
                  <label>Số thẻ
                    <input inputMode="numeric" value={cardInfo.number}
                      onChange={e => setCardInfo({ ...cardInfo, number: formatCard(e.target.value) })}
                      placeholder="#### #### #### ####" />
                  </label>
                  <label>Tên chủ thẻ
                    <input value={cardInfo.name}
                      onChange={e => setCardInfo({ ...cardInfo, name: e.target.value.toUpperCase() })}
                      placeholder="NGUYEN VAN A" />
                  </label>
                  <label>Hết hạn (MM/YY)
                    <input value={cardInfo.expiry} maxLength={5}
                      onChange={e => setCardInfo({ ...cardInfo, expiry: e.target.value.replace(/[^0-9/]/g, "").slice(0, 5) })}
                      placeholder="07/28" />
                  </label>
                  <label>CVV
                    <input value={cardInfo.cvv} inputMode="numeric" maxLength={3}
                      onChange={e => setCardInfo({ ...cardInfo, cvv: e.target.value.replace(/\D/g, "").slice(0, 3) })}
                      placeholder="***" />
                  </label>
                </div>
                <div className="txt-muted">* Demo giao diện — không lưu thông tin thẻ thật.</div>
              </div>
            )}

            {(method1 === "bank" || (split && method2 === "bank")) && (
              <div className="bankbox">
                <label>Mã giao dịch / Nội dung CK
                  <input value={bankRef} onChange={e => setBankRef(e.target.value)} placeholder="VD: CK 123ABC - Đơn #100045" />
                </label>
              </div>
            )}

            {(method1 === "qr" || (split && method2 === "qr")) && (
              <div className="qrbox">
                <div className="qrbox__code">QR CODE</div>
                <div className="txt-muted">* Hiển thị QR để khách quét (demo). Tổng cần thu: <b>{VND.format(total)}</b></div>
              </div>
            )}

            <div className="divider" />

            <div className="result">
              <div className="row"><span>Cần thu</span><b>{VND.format(total)}</b></div>
              <div className="row"><span>Đã nhận</span><b>{VND.format(received)}</b></div>
              <div className="row total"><span>Tiền thừa</span><b className={change > 0 ? "txt-green" : ""}>{VND.format(change)}</b></div>
            </div>

            <button className="btn xl" disabled={!covered || items.length === 0} onClick={onPay}>
              Xác nhận thanh toán
            </button>

            {!covered && <div className="error mt8">Số tiền nhận chưa đủ. Vui lòng kiểm tra lại.</div>}
            {items.length === 0 && <div className="error mt8">Giỏ hàng trống.</div>}
            {paid && <div className="success mt8">Đã thanh toán thành công! Bạn có thể in hóa đơn.</div>}
          </div>
        </section>
      </div>

      {/* Hidden receipt for printing */}
      <div className="receipt" ref={receiptRef} style={{ display: "none" }}>
        <div className="center">
          <h3>HÓA ĐƠN BÁN HÀNG</h3>
          <div className="muted">CRM Mobile Store</div>
          <div className="muted">Hotline: 09xx xxx xxx</div>
        </div>
        <div className="muted">Khách: {customer.name || "N/A"} — {customer.phone || "N/A"}</div>
        <table>
          <thead>
            <tr><th align="left">Sản phẩm</th><th align="right">SL</th><th align="right">Thành tiền</th></tr>
          </thead>
          <tbody>
            {items.map(it => (
              <tr key={it.id}>
                <td>{it.name}</td>
                <td align="right">{it.qty}</td>
                <td align="right">{VND.format(it.qty * it.price)}</td>
              </tr>
            ))}
          </tbody>
        </table>
        <div style={{ marginTop: 8 }}>
          <div>Tạm tính: <b>{VND.format(subtotal)}</b></div>
          <div>Giảm giá: <b>− {VND.format(discount)}</b></div>
          <div className="total">Tổng thanh toán: <b>{VND.format(total)}</b></div>
        </div>
        <div style={{ marginTop: 8 }}>
          <div>Phương thức 1: {labelOf(method1)} — {VND.format(pm1Amount)}</div>
          {split && <div>Phương thức 2: {labelOf(method2)} — {VND.format(pm2Amount)}</div>}
          <div>Tiền thừa: {VND.format(change)}</div>
        </div>
        <div className="center muted" style={{ marginTop: 10 }}>
          Cảm ơn Quý khách! Hẹn gặp lại.
        </div>
      </div>
    </div>
  );
}

function labelOf(m) {
  if (m === "cash") return "Tiền mặt";
  if (m === "card") return "Thẻ (POS)";
  if (m === "qr") return "QR Code";
  if (m === "bank") return "Chuyển khoản";
  return m;
}
