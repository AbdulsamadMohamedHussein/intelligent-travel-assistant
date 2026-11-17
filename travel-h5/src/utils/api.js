import request, { API_BASE } from './request';

/** Cities supported by the backend. */
export function getCities() {
  return request.get('/api/travel/cities');
}

/**
 * Get a trip recommendation.
 * @param {{city: string, budget: number, days: number}} data
 */
export function getTravelRecommend(data) {
  return request.post('/api/travel/recommend', data);
}

/** Save a trip (the server regenerates the plan from the same parameters). */
export function saveTrip(data) {
  return request.post('/api/trips', data);
}

export function getTrips() {
  return request.get('/api/trips');
}

export function getTrip(id) {
  return request.get(`/api/trips/${id}`);
}

export function deleteTrip(id) {
  return request.delete(`/api/trips/${id}`);
}

/**
 * Call a streaming (Server-Sent Events) endpoint and report each piece as it arrives.
 * The server sends lines like:  data:{"type":"chunk","content":"..."}  and finally  data:{"done":true}
 */
export async function fetchStream(url, data, onChunk, onComplete, onError) {
  let finished = false;
  const finish = () => {
    if (!finished) {
      finished = true;
      onComplete();
    }
  };
  const fail = (message) => {
    if (!finished) {
      finished = true;
      onError(message);
    }
  };

  try {
    const response = await fetch(`${API_BASE}/api/travel/${url}`, {
      method: 'POST',
      body: JSON.stringify(data),
      headers: { 'Content-Type': 'application/json' }
    });
    if (!response.ok || !response.body) {
      fail(`Server returned status ${response.status}`);
      return;
    }

    const reader = response.body.getReader();
    const decoder = new TextDecoder();
    let buffer = ''; // a network packet may end in the middle of a line, so keep the unfinished part

    while (true) {
      const { done, value } = await reader.read();
      if (done) break;
      buffer += decoder.decode(value, { stream: true });

      const lines = buffer.split('\n');
      buffer = lines.pop(); // the last piece may be incomplete
      for (const line of lines) {
        if (!line.startsWith('data:')) continue;
        const jsonStr = line.substring(5).trim();
        if (!jsonStr) continue;
        try {
          const jsonData = JSON.parse(jsonStr);
          if (jsonData.type === 'chunk') onChunk(jsonData.content);
          else if (jsonData.done) finish();
          else if (jsonData.error) fail(jsonData.error);
        } catch (e) {
          fail('Parse failed');
        }
      }
    }
    finish();
  } catch (error) {
    fail('Cannot connect to the server. Is the backend running?');
  }
}
