module.exports = {
  env: {
    browser: true,
    es2021: true,
    node: true,
  },
  extends: [
    'eslint:recommended',
    'plugin:react/recommended',
    'plugin:react-hooks/recommended',
    'plugin:jsx-a11y/recommended',
    // 'prettier', // Уберите эту строку, если не используете Prettier
  ],
  parserOptions: {
    ecmaFeatures: {
      jsx: true,
    },
    ecmaVersion: 'latest',
    sourceType: 'module',
  },
  plugins: [
    'react',
    'react-hooks',
    'jsx-a11y',
  ],
  settings: {
    react: {
      version: 'detect', // Автоматически определяет версию React
    },
  },
  rules: {
    // Основные правила JavaScript
    'no-console': 'warn',
    'no-unused-vars': 'warn',
    'no-var': 'error',
    'prefer-const': 'error',
    
    // React правила
    'react/react-in-jsx-scope': 'off', // Не требуется с React 17+
    'react/prop-types': 'off', // Можно включить, если используете prop-types
    'react/jsx-uses-react': 'off',
    'react/jsx-filename-extension': ['warn', { extensions: ['.jsx'] }],
    'react/self-closing-comp': 'warn',
    'react/jsx-props-no-spreading': 'off',
    
    // Хуки
    'react-hooks/rules-of-hooks': 'error',
    'react-hooks/exhaustive-deps': 'warn',
    
    // Доступность
    'jsx-a11y/anchor-is-valid': 'warn',
    'jsx-a11y/alt-text': 'warn',
    
    // Axios (дополнительные правила для работы с запросами)
    'no-promise-executor-return': 'off',
    'no-async-promise-executor': 'off',
  },
  overrides: [
    {
      files: ['**/*.test.js', '**/*.test.jsx'],
      env: {
        jest: true,
      },
    },
  ],
};