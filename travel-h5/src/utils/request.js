import axios from 'axios';

export const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:3200';

// An anonymous id stored in this browser. The backend uses it to keep each user's saved trips separate.
export function getUserId() {
  try {
    let id = localStorage.getItem('travel-user-id');
    if (!id) {
      id = 'u-' + Math.random().toString(36).slice(2) + Date.now().toString(36);
      localStorage.setItem('travel-user-id', id);
    }
    return id;
  } catch (e) {
    return 'anonymous';
  }
}

const request = axios.create({
  baseURL: API_BASE,
  timeout: 20000
});

// Request interceptor: attach the anonymous user id to every call
request.interceptors.request.use(
  (config) => {
    config.headers['X-User-Id'] = getUserId();
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor: return the unified Result body ({success, code, message, data})
request.interceptors.response.use(
  (response) => response.data,
  (error) => {
    console.error('Request error:', error.message);
    // The backend also sends a Result body with an error status (400/404/500); pass it on if present
    if (error.response && error.response.data && typeof error.response.data === 'object') {
      return Promise.resolve(error.response.data);
    }
    return Promise.reject(error);
  }
);

export default request;
