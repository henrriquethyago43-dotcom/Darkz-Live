import React, { useEffect, useRef, useState } from 'react';
import { ArrowLeft, Pause, Play, RotateCcw, Heart, MessageSquare, Gift, Zap, Shield, Sparkles } from 'lucide-react';
import { LiveEvent } from '../types';

interface GameCanvasProps {
  onBack: () => void;
  onTriggerEvent: (type: 'LIKE' | 'COMMENT' | 'GIFT' | 'SPECIAL_GIFT') => void;
  lastLiveEvent: LiveEvent | null;
  soundVolume: number;
}

interface Particle {
  x: number;
  y: number;
  vx: number;
  vy: number;
  color: string;
  alpha: number;
  radius: number;
  life: number;
  maxLife: number;
}

interface Projectile {
  x: number;
  y: number;
  vx: number;
  vy: number;
  isPlayer: boolean;
  damage: number;
  radius: number;
  color: string;
}

interface Enemy {
  id: string;
  x: number;
  y: number;
  vx: number;
  vy: number;
  width: number;
  height: number;
  health: number;
  maxHealth: number;
  scoreValue: number;
  type: 'drone' | 'interceptor' | 'cruiser' | 'sponsored';
  sponsorName?: string;
  shootTimer: number;
}

interface Boss {
  x: number;
  y: number;
  targetX: number;
  width: number;
  height: number;
  health: number;
  maxHealth: number;
  attackTimer: number;
  isCharging: boolean;
  name: string;
  summonedBy?: string;
}

interface FloatingNotice {
  id: string;
  text: string;
  x: number;
  y: number;
  color: string;
  alpha: number;
  life: number;
}

export const GameCanvas: React.FC<GameCanvasProps> = ({
  onBack,
  onTriggerEvent,
  lastLiveEvent,
  soundVolume
}) => {
  const canvasRef = useRef<HTMLCanvasElement | null>(null);

  const [score, setScore] = useState<number>(0);
  const [health, setHealth] = useState<number>(100);
  const [shield, setShield] = useState<number>(50);
  const [wave, setWave] = useState<number>(1);
  const [isPaused, setIsPaused] = useState<boolean>(false);
  const [isGameOver, setIsGameOver] = useState<boolean>(false);
  const [hasBoss, setHasBoss] = useState<boolean>(false);
  const [bossHp, setBossHp] = useState<number>(0);
  const [bossMaxHp, setBossMaxHp] = useState<number>(1000);
  const [spreadTimer, setSpreadTimer] = useState<number>(0);

  // Engine state refs to maintain uninterrupted 60fps loop
  const stateRef = useRef({
    player: {
      x: 180,
      y: 520,
      width: 44,
      height: 52,
      health: 100,
      maxHealth: 100,
      shield: 50,
      maxShield: 50,
      specialTimer: 0,
      invincibleTimer: 0
    },
    enemies: [] as Enemy[],
    boss: null as Boss | null,
    projectiles: [] as Projectile[],
    particles: [] as Particle[],
    notices: [] as FloatingNotice[],
    stars: [] as { x: number; y: number; speed: number; size: number }[],
    score: 0,
    wave: 1,
    waveKills: 0,
    fireTimer: 0,
    spawnTimer: 0,
    screenShake: 0,
    isPaused: false,
    isGameOver: false,
    width: 360,
    height: 640,
    isDragging: false
  });

  // Handle incoming live events
  useEffect(() => {
    if (!lastLiveEvent) return;
    const s = stateRef.current;

    if (lastLiveEvent.type === 'LIKE') {
      s.player.specialTimer = 6000;
      s.player.shield = Math.min(s.player.maxShield, s.player.shield + 15);
      s.score += 150;
      s.notices.push({
        id: Math.random().toString(),
        text: `👍 LIKE de ${lastLiveEvent.senderName}! CANHÃO TRIPLO!`,
        x: s.width / 2,
        y: s.height * 0.65,
        color: '#00F0FF',
        alpha: 1,
        life: 2000
      });
      setSpreadTimer(6000);
      setShield(Math.round(s.player.shield));
      setScore(s.score);
    } else if (lastLiveEvent.type === 'COMMENT') {
      s.enemies.push({
        id: Math.random().toString(),
        x: Math.random() * (s.width - 80) + 40,
        y: -40,
        vx: (Math.random() - 0.5) * 80,
        vy: 110,
        width: 48,
        height: 48,
        health: 180,
        maxHealth: 180,
        scoreValue: 400,
        type: 'sponsored',
        sponsorName: `@${lastLiveEvent.senderName}`,
        shootTimer: 1500
      });
      s.notices.push({
        id: Math.random().toString(),
        text: `💬 Invasor invocado por ${lastLiveEvent.senderName}!`,
        x: s.width / 2,
        y: s.height * 0.45,
        color: '#FFB800',
        alpha: 1,
        life: 2200
      });
    } else if (lastLiveEvent.type === 'GIFT') {
      s.projectiles = s.projectiles.filter(p => p.isPlayer);
      s.enemies.forEach(e => {
        e.health -= 120;
        createSparks(e.x, e.y, '#FF1A2A', 8);
      });
      s.enemies = s.enemies.filter(e => e.health > 0);
      if (s.boss) {
        s.boss.health -= 300;
      }
      s.screenShake = 18;
      s.score += 500;
      s.notices.push({
        id: Math.random().toString(),
        text: `🎁 BOMBA EMP (${lastLiveEvent.giftName || 'Presente'})! ÁREA LIMPA!`,
        x: s.width / 2,
        y: s.height * 0.5,
        color: '#00FF66',
        alpha: 1,
        life: 2400
      });
      setScore(s.score);
    } else if (lastLiveEvent.type === 'SPECIAL_GIFT') {
      spawnBoss(`@${lastLiveEvent.senderName}`);
      s.notices.push({
        id: Math.random().toString(),
        text: `👑 ALERTA: ${lastLiveEvent.senderName} INVOCOU O TITÃ GOLIATH!`,
        x: s.width / 2,
        y: 180,
        color: '#E50914',
        alpha: 1,
        life: 3000
      });
    }
  }, [lastLiveEvent]);

  const createSparks = (x: number, y: number, color: string, count: number) => {
    const s = stateRef.current;
    for (let i = 0; i < count; i++) {
      const angle = Math.random() * Math.PI * 2;
      const speed = Math.random() * 180 + 40;
      s.particles.push({
        x,
        y,
        vx: Math.cos(angle) * speed,
        vy: Math.sin(angle) * speed,
        color,
        alpha: 1,
        radius: Math.random() * 3 + 1.5,
        life: Math.random() * 400 + 200,
        maxLife: 600
      });
    }
  };

  const spawnBoss = (summonedBy?: string) => {
    const s = stateRef.current;
    s.boss = {
      x: s.width / 2,
      y: 110,
      targetX: s.width / 2,
      width: 140,
      height: 90,
      health: 1200 + s.wave * 400,
      maxHealth: 1200 + s.wave * 400,
      attackTimer: 1500,
      isCharging: false,
      name: 'DARKZ GOLIATH',
      summonedBy
    };
    s.screenShake = 22;
    setHasBoss(true);
    setBossHp(s.boss.health);
    setBossMaxHp(s.boss.maxHealth);
  };

  const restartGame = () => {
    const s = stateRef.current;
    s.player.x = s.width / 2;
    s.player.y = s.height * 0.8;
    s.player.health = 100;
    s.player.shield = 50;
    s.player.specialTimer = 0;
    s.enemies = [];
    s.boss = null;
    s.projectiles = [];
    s.particles = [];
    s.notices = [];
    s.score = 0;
    s.wave = 1;
    s.waveKills = 0;
    s.isPaused = false;
    s.isGameOver = false;

    setScore(0);
    setHealth(100);
    setShield(50);
    setWave(1);
    setIsPaused(false);
    setIsGameOver(false);
    setHasBoss(false);
    setSpreadTimer(0);
  };

  // Main 60 FPS Game Loop
  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const s = stateRef.current;
    s.width = canvas.width;
    s.height = canvas.height;
    s.player.x = s.width / 2;
    s.player.y = s.height * 0.8;

    // Stars
    s.stars = Array.from({ length: 65 }, () => ({
      x: Math.random() * s.width,
      y: Math.random() * s.height,
      speed: Math.random() * 80 + 30,
      size: Math.random() * 1.8 + 0.6
    }));

    let lastTime = performance.now();
    let animationId: number;

    const loop = (time: number) => {
      const dt = Math.min((time - lastTime) / 1000, 0.05);
      lastTime = time;

      if (!s.isPaused && !s.isGameOver) {
        // Timers
        if (s.screenShake > 0) {
          s.screenShake = Math.max(0, s.screenShake - dt * 35);
        }
        if (s.player.specialTimer > 0) {
          s.player.specialTimer -= dt * 1000;
          setSpreadTimer(Math.max(0, Math.round(s.player.specialTimer)));
        }
        if (s.player.invincibleTimer > 0) {
          s.player.invincibleTimer -= dt * 1000;
        }

        // Auto Firing
        s.fireTimer -= dt * 1000;
        if (s.fireTimer <= 0) {
          const isSpread = s.player.specialTimer > 0;
          if (isSpread) {
            s.projectiles.push(
              { x: s.player.x, y: s.player.y - 25, vx: 0, vy: -750, isPlayer: true, damage: 45, radius: 4, color: '#00F0FF' },
              { x: s.player.x - 12, y: s.player.y - 20, vx: -160, vy: -700, isPlayer: true, damage: 35, radius: 3.5, color: '#FF1A2A' },
              { x: s.player.x + 12, y: s.player.y - 20, vx: 160, vy: -700, isPlayer: true, damage: 35, radius: 3.5, color: '#FF1A2A' }
            );
            s.fireTimer = 130;
          } else {
            s.projectiles.push(
              { x: s.player.x - 10, y: s.player.y - 20, vx: 0, vy: -650, isPlayer: true, damage: 30, radius: 3.5, color: '#00F0FF' },
              { x: s.player.x + 10, y: s.player.y - 20, vx: 0, vy: -650, isPlayer: true, damage: 30, radius: 3.5, color: '#00F0FF' }
            );
            s.fireTimer = 210;
          }
        }

        // Stars
        s.stars.forEach(st => {
          st.y += st.speed * dt;
          if (st.y > s.height) {
            st.y = 0;
            st.x = Math.random() * s.width;
          }
        });

        // Spawn Enemies
        if (!s.boss) {
          s.spawnTimer -= dt * 1000;
          if (s.spawnTimer <= 0 && s.enemies.length < 5 + s.wave) {
            const types: ('drone' | 'interceptor' | 'cruiser')[] = ['drone', 'interceptor', 'cruiser'];
            const type = types[Math.floor(Math.random() * types.length)];
            const size = type === 'drone' ? 32 : type === 'interceptor' ? 38 : 46;
            const hp = type === 'drone' ? 35 : type === 'interceptor' ? 65 : 110;

            s.enemies.push({
              id: Math.random().toString(),
              x: Math.random() * (s.width - size * 2) + size,
              y: -30,
              vx: (Math.random() - 0.5) * 70,
              vy: Math.random() * 40 + 70 + s.wave * 8,
              width: size,
              height: size,
              health: hp,
              maxHealth: hp,
              scoreValue: hp * 2,
              type,
              shootTimer: Math.random() * 1500 + 1000
            });
            s.spawnTimer = Math.max(500, 1400 - s.wave * 80);
          }
        }

        // Update Enemies
        for (let i = s.enemies.length - 1; i >= 0; i--) {
          const e = s.enemies[i];
          e.x += e.vx * dt;
          e.y += e.vy * dt;

          if (e.x < e.width / 2 || e.x > s.width - e.width / 2) {
            e.vx = -e.vx;
          }

          e.shootTimer -= dt * 1000;
          if (e.shootTimer <= 0 && e.y < s.height * 0.7) {
            s.projectiles.push({
              x: e.x,
              y: e.y + e.height / 2,
              vx: 0,
              vy: 320,
              isPlayer: false,
              damage: 12,
              radius: 4,
              color: '#FFB800'
            });
            e.shootTimer = Math.random() * 2000 + 1500;
          }

          if (e.y > s.height + 40) {
            s.enemies.splice(i, 1);
            // Player loses small shield for breach
            s.player.shield = Math.max(0, s.player.shield - 10);
            setShield(Math.round(s.player.shield));
          }
        }

        // Update Boss
        if (s.boss) {
          const b = s.boss;
          b.x += (b.targetX - b.x) * (dt * 2.2);
          if (Math.abs(b.targetX - b.x) < 15) {
            b.targetX = Math.random() * (s.width - b.width) + b.width / 2;
          }

          b.attackTimer -= dt * 1000;
          if (b.attackTimer <= 0) {
            for (const angle of [-180, -90, 0, 90, 180]) {
              s.projectiles.push({
                x: b.x,
                y: b.y + b.height / 2,
                vx: angle,
                vy: 340,
                isPlayer: false,
                damage: 18,
                radius: 4.5,
                color: '#FF1A2A'
              });
            }
            b.attackTimer = 1600;
          }
          setBossHp(Math.max(0, Math.round(b.health)));
        }

        // Update Projectiles & Collisions
        for (let i = s.projectiles.length - 1; i >= 0; i--) {
          const p = s.projectiles[i];
          p.x += p.vx * dt;
          p.y += p.vy * dt;

          if (p.x < -10 || p.x > s.width + 10 || p.y < -20 || p.y > s.height + 20) {
            s.projectiles.splice(i, 1);
            continue;
          }

          if (p.isPlayer) {
            // Hit boss
            if (s.boss) {
              const b = s.boss;
              if (
                p.x >= b.x - b.width / 2 && p.x <= b.x + b.width / 2 &&
                p.y >= b.y - b.height / 2 && p.y <= b.y + b.height / 2
              ) {
                b.health -= p.damage;
                createSparks(p.x, p.y, '#FF1A2A', 4);
                s.projectiles.splice(i, 1);
                if (b.health <= 0) {
                  createSparks(b.x, b.y, '#E50914', 35);
                  s.score += 4000;
                  s.boss = null;
                  s.screenShake = 20;
                  s.wave += 1;
                  setHasBoss(false);
                  setScore(s.score);
                  setWave(s.wave);
                  s.notices.push({
                    id: Math.random().toString(),
                    text: '⭐ TITÃ DESTRUÍDO! +4000 PTS',
                    x: s.width / 2,
                    y: 180,
                    color: '#00FF66',
                    alpha: 1,
                    life: 2500
                  });
                }
                continue;
              }
            }

            // Hit normal enemy
            let hit = false;
            for (let j = s.enemies.length - 1; j >= 0; j--) {
              const e = s.enemies[j];
              const dist = Math.hypot(p.x - e.x, p.y - e.y);
              if (dist < e.width / 2 + p.radius) {
                e.health -= p.damage;
                createSparks(p.x, p.y, '#00F0FF', 3);
                s.projectiles.splice(i, 1);
                hit = true;

                if (e.health <= 0) {
                  createSparks(e.x, e.y, '#FFB800', 10);
                  s.score += e.scoreValue;
                  s.enemies.splice(j, 1);
                  s.waveKills += 1;
                  setScore(s.score);

                  if (s.waveKills >= 14 && !s.boss) {
                    s.waveKills = 0;
                    s.wave += 1;
                    setWave(s.wave);
                    if (s.wave % 3 === 0) {
                      spawnBoss();
                    }
                  }
                }
                break;
              }
            }
            if (hit) continue;
          } else {
            // Enemy projectile hits player
            const dist = Math.hypot(p.x - s.player.x, p.y - s.player.y);
            if (dist < s.player.width / 2 + p.radius) {
              if (s.player.invincibleTimer <= 0) {
                if (s.player.shield > 0) {
                  s.player.shield = Math.max(0, s.player.shield - p.damage);
                } else {
                  s.player.health = Math.max(0, s.player.health - p.damage);
                }
                s.player.invincibleTimer = 350;
                s.screenShake = 12;
                createSparks(p.x, p.y, '#E50914', 6);
                setHealth(Math.round(s.player.health));
                setShield(Math.round(s.player.shield));

                if (s.player.health <= 0) {
                  s.isGameOver = true;
                  setIsGameOver(true);
                }
              }
              s.projectiles.splice(i, 1);
            }
          }
        }

        // Update Particles
        for (let i = s.particles.length - 1; i >= 0; i--) {
          const pt = s.particles[i];
          pt.x += pt.vx * dt;
          pt.y += pt.vy * dt;
          pt.life -= dt * 1000;
          pt.alpha = Math.max(0, pt.life / pt.maxLife);
          if (pt.life <= 0) s.particles.splice(i, 1);
        }

        // Update Notices
        for (let i = s.notices.length - 1; i >= 0; i--) {
          const n = s.notices[i];
          n.y -= dt * 30;
          n.life -= dt * 1000;
          n.alpha = Math.max(0, n.life / 2000);
          if (n.life <= 0) s.notices.splice(i, 1);
        }
      }

      // RENDER CANVAS
      ctx.save();
      if (s.screenShake > 0) {
        ctx.translate((Math.random() - 0.5) * s.screenShake, (Math.random() - 0.5) * s.screenShake);
      }

      // Background
      ctx.fillStyle = '#050507';
      ctx.fillRect(0, 0, s.width, s.height);

      // Stars
      s.stars.forEach(st => {
        ctx.fillStyle = 'rgba(255, 255, 255, 0.65)';
        ctx.beginPath();
        ctx.arc(st.x, st.y, st.size, 0, Math.PI * 2);
        ctx.fill();
      });

      // Projectiles
      s.projectiles.forEach(p => {
        ctx.fillStyle = p.color;
        ctx.beginPath();
        ctx.arc(p.x, p.y, p.radius, 0, Math.PI * 2);
        ctx.fill();

        if (p.isPlayer) {
          ctx.fillStyle = p.color === '#00F0FF' ? 'rgba(0, 240, 255, 0.35)' : 'rgba(255, 26, 42, 0.35)';
          ctx.beginPath();
          ctx.arc(p.x, p.y, p.radius * 2.2, 0, Math.PI * 2);
          ctx.fill();
        }
      });

      // Enemies
      s.enemies.forEach(e => {
        const halfW = e.width / 2;
        const halfH = e.height / 2;
        const color = e.type === 'drone' ? '#FFB800' : e.type === 'interceptor' ? '#FF1A2A' : e.type === 'sponsored' ? '#00F0FF' : '#E50914';

        ctx.fillStyle = color;
        ctx.strokeStyle = '#FFFFFF';
        ctx.lineWidth = 1.5;

        ctx.beginPath();
        ctx.moveTo(e.x, e.y + halfH);
        ctx.lineTo(e.x + halfW, e.y - halfH);
        ctx.lineTo(e.x, e.y - halfH * 0.4);
        ctx.lineTo(e.x - halfW, e.y - halfH);
        ctx.closePath();
        ctx.fill();
        ctx.stroke();

        // Sponsor Tag
        if (e.sponsorName) {
          ctx.font = 'bold 9px monospace';
          ctx.fillStyle = '#00F0FF';
          ctx.textAlign = 'center';
          ctx.fillText(e.sponsorName, e.x, e.y - halfH - 8);
        }

        // Enemy Health Bar
        const barW = e.width;
        ctx.fillStyle = '#222';
        ctx.fillRect(e.x - barW / 2, e.y - halfH - 5, barW, 3);
        ctx.fillStyle = e.health / e.maxHealth > 0.4 ? '#00FF66' : '#FF1A2A';
        ctx.fillRect(e.x - barW / 2, e.y - halfH - 5, barW * (e.health / e.maxHealth), 3);
      });

      // Boss
      if (s.boss) {
        const b = s.boss;
        const halfW = b.width / 2;
        const halfH = b.height / 2;

        ctx.fillStyle = '#200005';
        ctx.strokeStyle = '#FF1A2A';
        ctx.lineWidth = 2.5;

        ctx.beginPath();
        ctx.moveTo(b.x, b.y + halfH);
        ctx.lineTo(b.x + halfW * 0.6, b.y + halfH * 0.7);
        ctx.lineTo(b.x + halfW, b.y);
        ctx.lineTo(b.x + halfW * 0.8, b.y - halfH);
        ctx.lineTo(b.x, b.y - halfH * 0.6);
        ctx.lineTo(b.x - halfW * 0.8, b.y - halfH);
        ctx.lineTo(b.x - halfW, b.y);
        ctx.lineTo(b.x - halfW * 0.6, b.y + halfH * 0.7);
        ctx.closePath();
        ctx.fill();
        ctx.stroke();

        // Core
        ctx.fillStyle = '#FF1A2A';
        ctx.beginPath();
        ctx.arc(b.x, b.y, 16, 0, Math.PI * 2);
        ctx.fill();

        ctx.fillStyle = '#FFF';
        ctx.beginPath();
        ctx.arc(b.x, b.y, 6, 0, Math.PI * 2);
        ctx.fill();
      }

      // Player Spacecraft
      const p = s.player;
      const pHalfW = p.width / 2;
      const pHalfH = p.height / 2;

      // Thruster flame
      ctx.fillStyle = 'rgba(0, 240, 255, 0.85)';
      ctx.beginPath();
      ctx.moveTo(p.x - 8, p.y + pHalfH);
      ctx.lineTo(p.x + 8, p.y + pHalfH);
      ctx.lineTo(p.x, p.y + pHalfH + Math.random() * 15 + 10);
      ctx.closePath();
      ctx.fill();

      // Delta ship
      ctx.fillStyle = '#E50914';
      ctx.strokeStyle = '#FFFFFF';
      ctx.lineWidth = 2;
      ctx.beginPath();
      ctx.moveTo(p.x, p.y - pHalfH);
      ctx.lineTo(p.x + pHalfW, p.y + pHalfH);
      ctx.lineTo(p.x + 8, p.y + pHalfH * 0.6);
      ctx.lineTo(p.x, p.y + pHalfH * 0.4);
      ctx.lineTo(p.x - 8, p.y + pHalfH * 0.6);
      ctx.lineTo(p.x - pHalfW, p.y + pHalfH);
      ctx.closePath();
      ctx.fill();
      ctx.stroke();

      // Cockpit
      ctx.fillStyle = '#00F0FF';
      ctx.beginPath();
      ctx.arc(p.x, p.y - 4, 5, 0, Math.PI * 2);
      ctx.fill();

      // Shield Aura
      if (p.shield > 0) {
        ctx.strokeStyle = `rgba(0, 240, 255, ${(p.shield / p.maxShield) * 0.5})`;
        ctx.lineWidth = 2;
        ctx.beginPath();
        ctx.arc(p.x, p.y, p.width * 0.8, 0, Math.PI * 2);
        ctx.stroke();
      }

      // Spread Laser Glow
      if (p.specialTimer > 0) {
        ctx.strokeStyle = 'rgba(255, 26, 42, 0.45)';
        ctx.lineWidth = 2.5;
        ctx.beginPath();
        ctx.arc(p.x, p.y, p.width * 1.05, 0, Math.PI * 2);
        ctx.stroke();
      }

      // Particles
      s.particles.forEach(pt => {
        ctx.fillStyle = pt.color;
        ctx.globalAlpha = pt.alpha;
        ctx.beginPath();
        ctx.arc(pt.x, pt.y, pt.radius, 0, Math.PI * 2);
        ctx.fill();
        ctx.globalAlpha = 1;
      });

      // Floating Notices
      s.notices.forEach(n => {
        ctx.font = 'bold 11px monospace';
        ctx.fillStyle = n.color;
        ctx.globalAlpha = n.alpha;
        ctx.textAlign = 'center';
        ctx.fillText(n.text, n.x, n.y);
        ctx.globalAlpha = 1;
      });

      ctx.restore();

      animationId = requestAnimationFrame(loop);
    };

    animationId = requestAnimationFrame(loop);
    return () => cancelAnimationFrame(animationId);
  }, []);

  // Pointer & Drag Handlers
  const handlePointerDown = (e: React.PointerEvent<HTMLCanvasElement>) => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const rect = canvas.getBoundingClientRect();
    const scaleX = canvas.width / rect.width;
    const scaleY = canvas.height / rect.height;
    const x = (e.clientX - rect.left) * scaleX;
    const y = (e.clientY - rect.top) * scaleY;

    stateRef.current.isDragging = true;
    stateRef.current.player.x = Math.max(30, Math.min(stateRef.current.width - 30, x));
    stateRef.current.player.y = Math.max(stateRef.current.height * 0.35, Math.min(stateRef.current.height - 40, y));
  };

  const handlePointerMove = (e: React.PointerEvent<HTMLCanvasElement>) => {
    if (!stateRef.current.isDragging) return;
    const canvas = canvasRef.current;
    if (!canvas) return;
    const rect = canvas.getBoundingClientRect();
    const scaleX = canvas.width / rect.width;
    const scaleY = canvas.height / rect.height;
    const x = (e.clientX - rect.left) * scaleX;
    const y = (e.clientY - rect.top) * scaleY;

    stateRef.current.player.x = Math.max(30, Math.min(stateRef.current.width - 30, x));
    stateRef.current.player.y = Math.max(stateRef.current.height * 0.35, Math.min(stateRef.current.height - 40, y));
  };

  const handlePointerUp = () => {
    stateRef.current.isDragging = false;
  };

  return (
    <div className="relative w-full h-full flex flex-col bg-[#050507] overflow-hidden select-none">
      {/* Top HUD */}
      <div className="absolute top-0 left-0 right-0 z-20 px-3 py-2 bg-gradient-to-b from-[#050507]/90 to-transparent">
        <div className="flex items-center justify-between">
          <button
            onClick={onBack}
            className="w-8 h-8 rounded-full bg-[#12121A]/80 border border-[#2A2A38] flex items-center justify-center text-white hover:border-[#E50914] transition-colors"
          >
            <ArrowLeft size={16} />
          </button>

          <div className="text-center">
            <div className="text-xs font-mono font-black text-[#FF1A2A] tracking-wider">
              ONDA {wave}
            </div>
            <div className="text-xs font-mono font-bold text-white">
              SCORE: {score.toLocaleString()}
            </div>
          </div>

          <button
            onClick={() => {
              stateRef.current.isPaused = !stateRef.current.isPaused;
              setIsPaused(stateRef.current.isPaused);
            }}
            className="w-8 h-8 rounded-full bg-[#12121A]/80 border border-[#2A2A38] flex items-center justify-center text-white hover:border-[#00F0FF] transition-colors"
          >
            {isPaused ? <Play size={15} /> : <Pause size={15} />}
          </button>
        </div>

        {/* Boss Health Bar if Active */}
        {hasBoss && (
          <div className="mt-2 p-1.5 bg-[#12121A]/90 border border-[#E50914] rounded">
            <div className="flex justify-between text-[10px] font-mono font-bold text-[#FF1A2A]">
              <span>👑 CHEFE: DARKZ GOLIATH</span>
              <span>{Math.round((bossHp / bossMaxHp) * 100)}%</span>
            </div>
            <div className="w-full bg-[#222] h-1.5 rounded-full overflow-hidden mt-1">
              <div
                className="bg-[#E50914] h-full transition-all duration-100"
                style={{ width: `${Math.max(0, (bossHp / bossMaxHp) * 100)}%` }}
              />
            </div>
          </div>
        )}

        {/* Player Bars (HP & Shield) */}
        <div className="mt-2 grid grid-cols-2 gap-2">
          <div>
            <div className="flex justify-between text-[9px] font-mono text-gray-300">
              <span className="text-[#FF1A2A] font-bold">HP</span>
              <span>{health}%</span>
            </div>
            <div className="w-full bg-[#1C1C28] h-1.5 rounded-full overflow-hidden">
              <div
                className={`h-full transition-all duration-75 ${health > 30 ? 'bg-[#00FF66]' : 'bg-[#FF1A2A]'}`}
                style={{ width: `${health}%` }}
              />
            </div>
          </div>

          <div>
            <div className="flex justify-between text-[9px] font-mono text-gray-300">
              <span className="text-[#00F0FF] font-bold">ESCUDO</span>
              <span>{shield}%</span>
            </div>
            <div className="w-full bg-[#1C1C28] h-1.5 rounded-full overflow-hidden">
              <div
                className="bg-[#00F0FF] h-full transition-all duration-75"
                style={{ width: `${shield * 2}%` }}
              />
            </div>
          </div>
        </div>

        {/* Powerup Timer Tag */}
        {spreadTimer > 0 && (
          <div className="mt-1 flex items-center justify-center gap-1 text-[9px] font-mono text-[#00F0FF] bg-[#00F0FF]/10 border border-[#00F0FF]/40 rounded py-0.5">
            <Sparkles size={10} />
            <span>DISPARO ESPECIAL ATIVO: {(spreadTimer / 1000).toFixed(1)}s</span>
          </div>
        )}
      </div>

      {/* Canvas */}
      <div className="flex-1 w-full relative touch-none">
        <canvas
          ref={canvasRef}
          width={360}
          height={600}
          onPointerDown={handlePointerDown}
          onPointerMove={handlePointerMove}
          onPointerUp={handlePointerUp}
          className="w-full h-full block cursor-crosshair"
        />
      </div>

      {/* Floating In-Game Event Simulator Bar */}
      <div className="absolute bottom-3 left-3 right-3 z-20">
        <div className="bg-[#12121A]/95 border border-[#2A2A38] rounded-xl p-2 shadow-2xl backdrop-blur-md">
          <div className="flex items-center justify-between text-[9px] font-mono text-gray-400 mb-1 px-1">
            <span className="text-[#FF1A2A] font-bold">SIMULADOR DE EVENTOS DA LIVE</span>
            <span>CLIQUE PARA DISPARAR</span>
          </div>
          <div className="grid grid-cols-4 gap-1.5">
            <button
              onClick={() => onTriggerEvent('LIKE')}
              className="flex flex-col items-center justify-center py-1.5 px-1 bg-[#FF1A2A]/15 border border-[#FF1A2A]/50 rounded-lg hover:bg-[#FF1A2A]/30 active:scale-95 transition-all"
            >
              <Heart size={14} className="text-[#FF1A2A]" />
              <span className="text-[9px] font-mono font-bold text-white mt-0.5">LIKE</span>
            </button>

            <button
              onClick={() => onTriggerEvent('COMMENT')}
              className="flex flex-col items-center justify-center py-1.5 px-1 bg-[#00F0FF]/15 border border-[#00F0FF]/50 rounded-lg hover:bg-[#00F0FF]/30 active:scale-95 transition-all"
            >
              <MessageSquare size={14} className="text-[#00F0FF]" />
              <span className="text-[9px] font-mono font-bold text-white mt-0.5">CHAT</span>
            </button>

            <button
              onClick={() => onTriggerEvent('GIFT')}
              className="flex flex-col items-center justify-center py-1.5 px-1 bg-[#FFB800]/15 border border-[#FFB800]/50 rounded-lg hover:bg-[#FFB800]/30 active:scale-95 transition-all"
            >
              <Gift size={14} className="text-[#FFB800]" />
              <span className="text-[9px] font-mono font-bold text-white mt-0.5">GIFT</span>
            </button>

            <button
              onClick={() => onTriggerEvent('SPECIAL_GIFT')}
              className="flex flex-col items-center justify-center py-1.5 px-1 bg-[#E50914]/20 border border-[#E50914] rounded-lg hover:bg-[#E50914]/40 active:scale-95 transition-all"
            >
              <Zap size={14} className="text-[#E50914]" />
              <span className="text-[9px] font-mono font-bold text-white mt-0.5">CHEFE</span>
            </button>
          </div>
        </div>
      </div>

      {/* Game Over Modal */}
      {isGameOver && (
        <div className="absolute inset-0 z-30 bg-black/85 flex flex-col items-center justify-center p-6 text-center backdrop-blur-sm">
          <div className="w-16 h-16 rounded-full bg-[#E50914]/20 border border-[#E50914] flex items-center justify-center mb-3">
            <Zap size={32} className="text-[#E50914]" />
          </div>
          <h2 className="text-2xl font-mono font-black text-[#FF1A2A] tracking-wider">GAME OVER</h2>
          <p className="text-xs text-gray-300 mt-1">Sua nave foi destruída no espaço profundo.</p>
          <div className="my-4 p-3 bg-[#12121A] border border-[#2A2A38] rounded-lg w-full max-w-[220px]">
            <div className="text-[10px] font-mono text-gray-400">PONTUAÇÃO</div>
            <div className="text-lg font-mono font-black text-[#00F0FF]">{score.toLocaleString()}</div>
            <div className="text-[10px] font-mono text-gray-400 mt-1">ONDA FINAL: {wave}</div>
          </div>
          <div className="flex gap-2 w-full max-w-[220px]">
            <button
              onClick={restartGame}
              className="flex-1 py-2 bg-[#E50914] hover:bg-[#B8000A] text-white font-mono font-bold text-xs rounded border border-white/50 flex items-center justify-center gap-1.5"
            >
              <RotateCcw size={14} /> REINICIAR
            </button>
            <button
              onClick={onBack}
              className="py-2 px-3 bg-[#1C1C28] hover:bg-[#2A2A38] text-gray-300 font-mono font-bold text-xs rounded border border-[#383848]"
            >
              MENU
            </button>
          </div>
        </div>
      )}

      {/* Paused Modal */}
      {isPaused && !isGameOver && (
        <div className="absolute inset-0 z-30 bg-black/80 flex flex-col items-center justify-center p-6 text-center backdrop-blur-sm">
          <h2 className="text-xl font-mono font-black text-white tracking-wider">JOGO PAUSADO</h2>
          <p className="text-xs text-gray-400 mt-1">A live continua conectada em segundo plano.</p>
          <div className="flex gap-2 mt-4">
            <button
              onClick={() => {
                stateRef.current.isPaused = false;
                setIsPaused(false);
              }}
              className="px-5 py-2 bg-[#E50914] text-white font-mono font-bold text-xs rounded"
            >
              CONTINUAR
            </button>
            <button
              onClick={onBack}
              className="px-4 py-2 bg-[#1C1C28] text-gray-300 font-mono font-bold text-xs rounded"
            >
              SAIR
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
