
## Reporting security issues

Please report security issues related to the logback-access project to
the following email address:

   support(at)qos.ch


## Verifying contents

All logback-access project artifacts published on Maven central are
signed. For each artifact, there is an associated signature file with
the .asc suffix.

The cryptographic key was updated 2022-08-08 to use a more modern
Elliptic curve algorithm instead of RSA previously.

### After 2022-08-08

To verify the signature use [this public key](https://www.slf4j.org/public-keys/60200AC4AE761F1614D6C46766D68DAA073BE985.gpg). Here is its fingerprint:
```
pub   nistp521 2022-08-08 [SC]
      60200AC4AE761F1614D6C46766D68DAA073BE985
uid   Ceki Gulcu <ceki@qos.ch>
sub   nistp521 2022-08-08 [E]
```

A copy of this key is stored on the
[keys.openpgp.org](https://keys.openpgp.org) keyserver. To add it to
your public key ring use the following command:

```
> FINGER_PRINT=60200AC4AE761F1614D6C46766D68DAA073BE985
> gpg  --keyserver hkps://keys.openpgp.org --recv-keys $FINGER_PRINT
```


## Preventing commit history overwrite

In order to prevent loss of commit history, developers of the project
are highly encouraged to deny branch deletions or history overwrites
by invoking the following two commands on their local copy of the
repository.


```
git config receive.denyDelete true
git config receive.denyNonFastForwards true
```