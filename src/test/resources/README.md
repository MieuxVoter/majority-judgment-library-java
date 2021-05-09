Do add your test cases in the JSON file.

Some of the sample tallies were made using python.

```python
import numpy as np
def randofsum_unbalanced(s, n):
    # Where s = sum (e.g. 40 in your case) and n is the output array length (e.g. 4 in your case)
    r = np.random.rand(n)
    a = np.array(np.round((r/np.sum(r))*s,0),dtype=int)
    while np.sum(a) > s:
        a[np.random.choice(n)] -= 1
    while np.sum(a) < s:
        a[np.random.choice(n)] += 1
    return a
```
