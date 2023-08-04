var CACHE_NAME = 'my-app-cache-v1';
var urlsToCache = [
    // '/',
    '/index.html',
    '/favicon.ico',
    //'/css/style.css',
    '/js/main.js',
    // Add other assets as needed
];

self.addEventListener('install', function(event) {
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then(function(cache) {
        return Promise.all(
          urlsToCache.map(function(url) {
            return fetch(url)
              .then(function(response) {
                if (!response.ok) {
                  throw new Error('Failed to fetch ' + url);
                }
                return cache.put(url, response);
              })
          })
        );
      })
      .catch(function(error) {
        console.error('Failed to cache:', error);
      })
  );
});

self.addEventListener('fetch', function(event) {
  event.respondWith(
    caches.match(event.request)
      .then(function(response) {
        if (response) {
          return response; // Return cached response if available
        }
        return fetch(event.request); // Otherwise, fetch from the network
      })
  );
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
