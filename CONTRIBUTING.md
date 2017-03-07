## Coding Conventions

Use `//` for all comments except license headers and class / method documentation.
The latter uses `/**` with `*/` and requires an asterik on each line.

Imports from different packages should have a blank line in between.

```
/**
 * ... LICENSE HEADER ...
 */

import javax.swing.JFrame;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean; // No new line here because its still from java.util
```

Indent pattern is 4 spaces, tabs, and then tab with 4 spaces, and then 2 tabs, and so on...

```
public class A {

// Four spaces here
    public static void main (String[] args) {
	// Tab here
	if (true) {
	    // Tab with four spaces
	    System.out.println ("From A");
	}
    }
}
```

A space in between an opening parenthesis and a keyword

```
if (true) {
}

while (true) {
}

for (int i = 0; i < 10; ++i) {
}
```

Wrap long function calls to the parenthesis. Padding is done with spaces.

An extra level of indenting for anonymous classes.

```
redBox = new Sprite(ImageIO.read(Main.class.getResourceAsStream("red_box.bmp")),
                    new Point(50, 75))
    {
	@Override
	public void onCollision (Sprite other) {
	    // ... do stuff ...
	}
    };
```

Avoid using lambdas and member references introduced in java 8

```
// Prefer this
for (Item item : items) {
    // ... do stuff ...
}

// Avoid this
items.forEach (item -> {
    // ... do stuff ...
});
```

Space between infix operators. None after prefix and before postfix operators.

## Other stuff?

Remember to add your info in the `CONTRIBUTORS.txt`.
