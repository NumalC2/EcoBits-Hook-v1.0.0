# EcoBits Hook v1.0.0

Development-server-tested release for X-Prison 2026.3.15.1 and EcoBits 2026.45.

The packaged binary is the tested v0.2.2-beta implementation with its addon manifest version changed to 1.0.0; no Java logic changes were introduced.

## Installation
1. Back up your server and EcoBits data. Stop the server.
2. Install `EcoBits-Hook-v1.0.0.jar` in to the `plugins/X-Prison/addons/` folder.
3. Start the server. Check for `Loaded addon: EcoBits Hook v1.0.0` and currency registration logs.
4. Add GUI currency buttons manually in `plugins/X-Prison/enchants.yml` under `enchant_menu.currency-enchants-items` for custom currencies used by enchantments.

## Supported features
- Automatically discovers and registers EcoBits currencies.
- Supports decimal and integer-only currencies.
- Integer-only: rounds fractional costs up for checks/withdrawals, fractional deposits down.
- Does not maintain duplicate balances.
- Periodic currency synchronization.

## Known limitations
- The enchant GUI does not automatically create tabs for new currencies.
- The GUI may show fractional prices for integer-only currencies even though the actual charge rounds up.
- Runtime verification was performed by the user on their development server; no new production-scale load or concurrency tests were performed for this release.
- Large floating-point values may lose precision at the X-Prison API boundary.
- The Maven POM may require locally installed API dependencies for independent builds.

## Release notes
See CHANGELOG.md.
