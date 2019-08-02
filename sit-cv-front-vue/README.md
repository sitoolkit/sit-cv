# sit-cv-front-vue

## Project setup
```
yarn install
```

### Compiles and hot-reloads for development
```
yarn run serve
```

### Compiles and minifies for production
```
yarn run build
```

### Run your tests
```
yarn run test
```

### Lints and fixes files
```
yarn run lint
```

### Customize UIKit Theme

Obtain the uikit material with the following command. Remove uikit's .git and .gitignore.

```
cd etc/uikit
git init
git remote add origin git://github.com/uikit/uikit.git
git pull --tags
git checkout v3.1.5
yarn install

# Windows
rmdir /s /q .git
del .gitignore

# Mac
rm -rf .git
rm .gitignore
```

Next, edit the compile-less script in etc/uikit/package.json.

```
{
  "scripts": {
    // Windows
    "compile-less": "yarn icons && node build/less && copy dist\\css\\uikit.sit-cv-theme.min.css ..\\..\\src\\assets",

    // Mac
    "compile-less": "yarn icons && node build/less && cp dist/css/uikit.sit-cv-theme.min.css ../../src/assets",
  }
}
```

And execute the following command from etc/uikit directory.

```
yarn watch
```

### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).
