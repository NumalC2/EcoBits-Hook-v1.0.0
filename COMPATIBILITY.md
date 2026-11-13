Verified by inspecting uploaded JAR manifests and javap signatures:
X-Prison 2026.3.15.1; EcoBits 2026.45.

This is source-only, not compiled or tested in a running server. Requires Bukkit/Paper API and eco dependencies at build/runtime. Ensure Maven dependencies are available locally or configure their repositories.

EcoBits currency IDs can conflict with existing X-Prison currencies; resolve duplicates before deploying. New currency registrations are polled every 100 ticks; changes depend on X-Prison addon lifecycle and plugin startup order. Test reload and shutdown behavior.
