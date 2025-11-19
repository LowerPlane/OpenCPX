/**
 * OpenCPX HTTP Handlers
 */

import { VERSION, PostureProvider } from './types';
import { Posture } from './models';

/**
 * Create an Express middleware for the /cpx endpoint
 */
export function createExpressHandler(provider: PostureProvider) {
  return async (req: any, res: any) => {
    try {
      const posture = await provider();
      res.setHeader('Content-Type', 'application/json');
      res.setHeader('X-CPX-Version', VERSION);
      res.json(posture.toObject());
    } catch (error) {
      res.status(500).json({ error: 'Internal server error' });
    }
  };
}

/**
 * Create an Express middleware that adds /cpx to existing routes
 */
export function createExpressMiddleware(provider: PostureProvider) {
  const handler = createExpressHandler(provider);

  return (req: any, res: any, next: any) => {
    if (req.path === '/cpx' && req.method === 'GET') {
      return handler(req, res);
    }
    next();
  };
}

/**
 * Create a Koa middleware for the /cpx endpoint
 */
export function createKoaHandler(provider: PostureProvider) {
  return async (ctx: any) => {
    try {
      const posture = await provider();
      ctx.set('Content-Type', 'application/json');
      ctx.set('X-CPX-Version', VERSION);
      ctx.body = posture.toObject();
    } catch (error) {
      ctx.status = 500;
      ctx.body = { error: 'Internal server error' };
    }
  };
}

/**
 * Create a Fastify handler for the /cpx endpoint
 */
export function createFastifyHandler(provider: PostureProvider) {
  return async (request: any, reply: any) => {
    try {
      const posture = await provider();
      reply
        .header('Content-Type', 'application/json')
        .header('X-CPX-Version', VERSION)
        .send(posture.toObject());
    } catch (error) {
      reply.status(500).send({ error: 'Internal server error' });
    }
  };
}

/**
 * Create a generic HTTP handler (works with Node.js http module)
 */
export function createHttpHandler(provider: PostureProvider) {
  return async (req: any, res: any) => {
    if (req.method !== 'GET') {
      res.writeHead(405, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ error: 'Method not allowed' }));
      return;
    }

    try {
      const posture = await provider();
      res.writeHead(200, {
        'Content-Type': 'application/json',
        'X-CPX-Version': VERSION,
      });
      res.end(posture.toJSON(2));
    } catch (error) {
      res.writeHead(500, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ error: 'Internal server error' }));
    }
  };
}

/**
 * Create a Next.js API handler
 */
export function createNextHandler(provider: PostureProvider) {
  return async (req: any, res: any) => {
    if (req.method !== 'GET') {
      res.status(405).json({ error: 'Method not allowed' });
      return;
    }

    try {
      const posture = await provider();
      res.setHeader('X-CPX-Version', VERSION);
      res.status(200).json(posture.toObject());
    } catch (error) {
      res.status(500).json({ error: 'Internal server error' });
    }
  };
}
