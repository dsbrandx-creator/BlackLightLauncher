from PIL import Image, ImageFilter, ImageOps, ImageChops

# 1. Load your base Phoenix image
try:
    img = Image.open("phoenix_base.png").convert("RGBA")
except FileNotFoundError:
    print("ERROR: Please save your Black-Light Phoenix logo as 'phoenix_base.png' in this folder.")
    exit()

# 2. Convert to Shining Silver (Metallic Grayscale)
gray = ImageOps.grayscale(img)
silver = ImageOps.colorize(gray, black="#000000", white="#E0E0E0").convert("RGBA")

# 3. Create the Purple Glow (An exact copy of the image, filled with purple)
glow = Image.new("RGBA", img.size, (155, 0, 255, 150))
glow = glow.filter(ImageFilter.GaussianBlur(40))

# 4. Composite: Put the silver phoenix ON TOP of the purple glow
# We use ImageChops.add to merge the light from both images, ignoring black backgrounds
final = ImageChops.add(glow, silver)

# 5. Save
final.save("phoenix_glowing_silver.png")
print("SUCCESS: Your Shining Silver Phoenix has been created.")