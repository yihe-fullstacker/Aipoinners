// utils/validate.js — 表单校验，被 pages/login/login.vue + pages/register/register.vue 调用
export function isValidPhone(p) { return /^1[3-9]\d{9}$/.test(p) }
export function isValidPassword(p) { return p && p.length >= 6 }
export function isValidUsername(u) { return u && u.length >= 2 && u.length <= 50 }
export function isValidEmail(e) { return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(e) }
