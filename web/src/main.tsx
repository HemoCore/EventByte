import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import { App } from './App.tsx'
import { getEnv } from './lib/env.ts';
import { loadConfig } from './config/config-loader.ts';
import { createStore } from './core/create-store.ts';
import { createRouter } from './router.ts';

const env = getEnv();
const config = await loadConfig(env);

const store = createStore(config.dependencies);
const router = createRouter({
    dispatch: store.dispatch,
});

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <App store={store} stripe={config.stripe} router={router} />
    </StrictMode>,
)
