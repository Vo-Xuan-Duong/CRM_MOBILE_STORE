// CustomerForm.jsx
import './CustomerForm.css';
import React, { useState, useEffect } from 'react';
import { X, Save, User, Phone, Mail, MapPin, Calendar, Building } from 'lucide-react';

const CustomerForm = ({ 
  isOpen, 
  onClose, 
  onSave, 
  customer = null, 
  title = "Add New Customer" 
}) => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    address: '',
    dateOfBirth: '',
    company: '',
    notes: ''
  });

  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (customer) {
      setFormData({
        name: customer.name || '',
        email: customer.email || '',
        phone: customer.phone || '',
        address: customer.address || '',
        dateOfBirth: customer.dateOfBirth || '',
        company: customer.company || '',
        notes: customer.notes || ''
      });
    } else {
      setFormData({
        name: '',
        email: '',
        phone: '',
        address: '',
        dateOfBirth: '',
        company: '',
        notes: ''
      });
    }
    setErrors({});
  }, [customer, isOpen]);

  const validateForm = () => {
    const newErrors = {};

    if (!formData.name.trim()) {
      newErrors.name = 'Name is required';
    }

    if (!formData.email.trim()) {
      newErrors.email = 'Email is required';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      newErrors.email = 'Please enter a valid email address';
    }

    if (!formData.phone.trim()) {
      newErrors.phone = 'Phone number is required';
    } else if (!/^\+?[\d\s\-\(\)]+$/.test(formData.phone)) {
      newErrors.phone = 'Please enter a valid phone number';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));

    // Clear error when user starts typing
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    if (validateForm()) {
      const customerData = {
        ...formData,
        id: customer?.id || Date.now(), // Generate ID for new customers
        createdAt: customer?.createdAt || new Date().toISOString(),
        updatedAt: new Date().toISOString()
      };
      
      onSave(customerData);
      onClose();
    }
  };


  if (!isOpen) return null;

  return (
    <div className="customer-form-overlay">
      <div className="customer-form-modal">
        {/* Header */}
        <div className="form-header">
          <h2 className="form-title">{title}</h2>
          <button
            onClick={onClose}
            className="close-button"
            type="button"
          >
            <X size={22} />
          </button>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="form-content form-grid">
          {/* Name */}
          <div className="form-group">
            <label className="form-label">
              <User size={18} style={{marginRight: 6}} />
              Full Name <span className="required">*</span>
            </label>
            <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleInputChange}
              className={`form-input${errors.name ? ' error' : ''}`}
              placeholder="Enter customer's full name"
            />
            {errors.name && <span className="error-message">{errors.name}</span>}
          </div>

          {/* Email */}
          <div className="form-group">
            <label className="form-label">
              <Mail size={18} style={{marginRight: 6}} />
              Email Address <span className="required">*</span>
            </label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleInputChange}
              className={`form-input${errors.email ? ' error' : ''}`}
              placeholder="Enter email address"
            />
            {errors.email && <span className="error-message">{errors.email}</span>}
          </div>

          {/* Phone */}
          <div className="form-group">
            <label className="form-label">
              <Phone size={18} style={{marginRight: 6}} />
              Phone Number <span className="required">*</span>
            </label>
            <input
              type="tel"
              name="phone"
              value={formData.phone}
              onChange={handleInputChange}
              className={`form-input${errors.phone ? ' error' : ''}`}
              placeholder="Enter phone number"
            />
            {errors.phone && <span className="error-message">{errors.phone}</span>}
          </div>

          {/* Address */}
          <div className="form-group">
            <label className="form-label">
              <MapPin size={18} style={{marginRight: 6}} />
              Address
            </label>
            <textarea
              name="address"
              value={formData.address}
              onChange={handleInputChange}
              rows={3}
              className="form-textarea"
              placeholder="Enter customer's address"
            />
          </div>

          {/* Date of Birth & Company */}
          <div className="form-row">
            <div className="form-group">
              <label className="form-label">
                <Calendar size={18} style={{marginRight: 6}} />
                Date of Birth
              </label>
              <input
                type="date"
                name="dateOfBirth"
                value={formData.dateOfBirth}
                onChange={handleInputChange}
                className="form-input"
              />
            </div>

            <div className="form-group">
              <label className="form-label">
                <Building size={18} style={{marginRight: 6}} />
                Company
              </label>
              <input
                type="text"
                name="company"
                value={formData.company}
                onChange={handleInputChange}
                className="form-input"
                placeholder="Company name"
              />
            </div>
          </div>

          {/* Notes */}
          <div className="form-group">
            <label className="form-label">Notes</label>
            <textarea
              name="notes"
              value={formData.notes}
              onChange={handleInputChange}
              rows={4}
              className="form-textarea"
              placeholder="Additional notes about the customer..."
            />
          </div>

          {/* Form Actions */}
          <div className="form-footer">
            <button
              type="button"
              onClick={onClose}
              className="form-button secondary"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="form-button primary"
            >
              <Save size={18} style={{marginRight: 6}} />
              {customer ? 'Update Customer' : 'Add Customer'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CustomerForm;