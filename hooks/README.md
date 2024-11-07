# your app Application Hooks

Our external application can be written by anyone.

This is a great advantage for everyone.

But with great power there must also come great responsibility!

In order to ensure your app compatibility with BigID there must be several validations.

This folder contains the following git-hooks:

- pre-commit - check the project structure and the docker's base-url.

## How to set git use the hooks

In order set git run the hooks you should run the following command:

``git config core.hooksPath ./hooks``

This will set the git-hooks folder use our custom hooks and will let you know if something wrong.
