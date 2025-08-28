import React, { useRef } from "react";
import * as XLSX from "xlsx";

export default function ProductImportExcel({ onImport }) {
  const fileInput = useRef();

  const handleFile = (e) => {
    const file = e.target.files[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = (evt) => {
      const data = new Uint8Array(evt.target.result);
      const workbook = XLSX.read(data, { type: "array" });
      const sheet = workbook.Sheets[workbook.SheetNames[0]];
      const rows = XLSX.utils.sheet_to_json(sheet, { defval: "" });
      if (typeof onImport === "function") onImport(rows);
    };
    reader.readAsArrayBuffer(file);
  };

  return (
    <div style={{ display: "inline-block" }}>
      <input
        type="file"
        accept=".xlsx,.xls,.csv"
        ref={fileInput}
        style={{ display: "none" }}
        onChange={handleFile}
      />
      <button
        className="btn btn-primary btn-with-icon"
        onClick={() => fileInput.current && fileInput.current.click()}
        type="button"
      >
        Nhập Từ Excel
      </button>
    </div>
  );
}
