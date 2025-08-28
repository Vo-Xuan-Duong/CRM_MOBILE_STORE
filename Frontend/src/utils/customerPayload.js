export function normalizeCustomerPayload(input = {}) {
  const toNull = (v) => (v === "" || v === undefined ? null : v);

  return {
    fullName: input.fullName?.trim() ?? "",        // required
    phone: input.phone?.trim() ?? "",              // required
    email: toNull(input.email?.trim()),
    birthday: input.birthday ? String(input.birthday).slice(0, 10) : null,
    gender: input.gender || "NA",                  // MALE|FEMALE|NA
    fullAddress: toNull(input.fullAddress),
    note: toNull(input.note),
    tier: input.tier || "REGULAR",                 // REGULAR|VIP|...
  };
}