import { spawn } from "child_process";

const version = process.argv[2] || "26.4";
const task = `:${version}:runClient`;

console.log(`🚀 Launching Minecraft ${version} with Autotool mod...`);
const child = spawn("./gradlew", [task], { stdio: "inherit" });

child.on("exit", (code) => {
	process.exit(code ?? 0);
});
