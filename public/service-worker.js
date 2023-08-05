var CACHE_NAME = 'my-app-cache-v1';
var urlsToCache = [
    // '/',
    '/index.html',
    '/favicon.ico',
    '/icons/icon-192x192.png',
    '/icons/icon-512x512.png',
    '/manifest.json',
    '/js/main.js',
];


self.addEventListener('install', function(event) {
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then(function(cache) {
        return Promise.all(urlsToCache.map(function(url) {
          return fetch(url).then(function(response) {
            if (!response.ok) {
              console.error('Failed to cache:', url, response.status, response.statusText);
              return; // Skip this URL if fetch failed
            }
            return cache.put(url, response);
          }).catch(function(error) {
            console.error('Fetch error for:', url, error);
          });
        }));
      })
  );
});


self.addEventListener('fetch', function(event) {
  if (event.request.mode === 'navigate') {
    event.respondWith(
      caches.match('/index.html')
        .then(function(response) {
          return response || fetch(event.request);
        })
    );
  } else {
    event.respondWith(
      caches.match(event.request)
        .then(function(response) {
          return response || fetch(event.request);
        })
    );
  }
});


self.addEventListener('activate', function(event) {
  var cacheWhitelist = [CACHE_NAME]; // Keep the current cache version
  event.waitUntil(
    caches.keys().then(function(cacheNames) {
      return Promise.all(
        cacheNames.map(function(cacheName) {
          if (cacheWhitelist.indexOf(cacheName) === -1) {
            return caches.delete(cacheName); // Delete old caches
          }
        })
      );
    })
  );
});
