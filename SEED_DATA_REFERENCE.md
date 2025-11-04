# SDV Developer Console - Seed Data Reference

## ✅ Database Successfully Populated!

You now have **16 scenarios** and **13 tracks** ready to use in your simulations via the UI at http://localhost:3000

---

## 📋 Available Scenarios

### Urban Driving (4 scenarios)
1. **Urban Traffic Navigation** (`550e8400-e29b-41d4-a716-446655440001`)
   - Navigate through dense city traffic with multiple intersections and traffic lights

2. **Pedestrian Crossing Detection** (`550e8400-e29b-41d4-a716-446655440002`)
   - Detect and respond to pedestrians crossing at marked and unmarked crossings

3. **Parking Maneuvers** (`550e8400-e29b-41d4-a716-446655440003`)
   - Test automated parking in various scenarios including parallel, perpendicular, and angle parking

4. **Roundabout Navigation** (`550e8400-e29b-41d4-a716-446655440004`)
   - Navigate multi-lane roundabouts with yielding to traffic

### Highway (4 scenarios)
5. **Highway Lane Keeping** (`550e8400-e29b-41d4-a716-446655440005`)
   - Maintain lane position at highway speeds with lane departure warnings

6. **Adaptive Cruise Control** (`550e8400-e29b-41d4-a716-446655440006`)
   - Test adaptive cruise control with varying traffic density and speeds

7. **Lane Change Assist** (`550e8400-e29b-41d4-a716-446655440007`)
   - Automated lane changes with blind spot detection and safe gap finding

8. **Highway Merging** (`550e8400-e29b-41d4-a716-446655440008`)
   - Merge onto highway from on-ramp with traffic flow analysis

### Safety (4 scenarios)
9. **Emergency Braking** (`550e8400-e29b-41d4-a716-446655440009`)
   - Test automatic emergency braking with various obstacles and speeds

10. **Collision Avoidance** (`550e8400-e29b-41d4-a716-446655440010`)
    - Avoid collisions with sudden obstacles and crossing vehicles

11. **Vulnerable Road Users** (`550e8400-e29b-41d4-a716-446655440011`)
    - Detect and protect cyclists, motorcyclists, and pedestrians

12. **School Zone Safety** (`550e8400-e29b-41d4-a716-446655440016`)
    - Reduced speed in school zones with child detection

### Weather (3 scenarios)
13. **Rain Driving** (`550e8400-e29b-41d4-a716-446655440012`)
    - Test sensor performance and vehicle control in heavy rain conditions

14. **Fog Navigation** (`550e8400-e29b-41d4-a716-446655440013`)
    - Navigate with reduced visibility in foggy conditions

15. **Night Driving** (`550e8400-e29b-41d4-a716-446655440014`)
    - Test headlight adaptive systems and low-light sensor performance

### Edge Cases (1 scenario)
16. **Construction Zone Navigation** (`550e8400-e29b-41d4-a716-446655440015`)
    - Navigate through construction zones with lane shifts and temporary signs

---

## 🛣️ Available Tracks

### Urban Tracks
1. **Downtown City Circuit** (`660e8400-e29b-41d4-a716-446655440001`) - 15 minutes
   - Dense urban environment with traffic lights, intersections, and pedestrian crossings

2. **Suburban Route** (`660e8400-e29b-41d4-a716-446655440002`) - 20 minutes
   - Mixed residential and commercial areas with moderate traffic

3. **Urban Parking Complex** (`660e8400-e29b-41d4-a716-446655440003`) - 10 minutes
   - Multi-story parking facility with various parking scenarios

### Highway Tracks
4. **Autobahn Test Section** (`660e8400-e29b-41d4-a716-446655440004`) - 30 minutes
   - High-speed highway section with multiple lanes and on/off ramps

5. **Highway Interchange Complex** (`660e8400-e29b-41d4-a716-446655440005`) - 25 minutes
   - Complex highway interchange with multiple merging and diverging lanes

### Safety Tracks
6. **Emergency Response Track** (`660e8400-e29b-41d4-a716-446655440006`) - 12 minutes
   - Dedicated track for emergency braking and collision avoidance tests

7. **Vulnerable User Circuit** (`660e8400-e29b-41d4-a716-446655440007`) - 18 minutes
   - Track designed for testing interactions with pedestrians and cyclists

### Weather Tracks
8. **Weather Simulation Track** (`660e8400-e29b-41d4-a716-446655440008`) - 22 minutes
   - Track with artificial rain, fog, and lighting control systems

9. **Night Testing Circuit** (`660e8400-e29b-41d4-a716-446655440009`) - 20 minutes
   - Track optimized for night driving and low-light condition testing

### Mixed Tracks
10. **Country Road Mix** (`660e8400-e29b-41d4-a716-446655440010`) - 35 minutes
    - Combination of rural roads, small towns, and varying road conditions

11. **Alpine Route** (`660e8400-e29b-41d4-a716-446655440011`) - 40 minutes
    - Mountain roads with curves, elevation changes, and scenic routes

### Comprehensive Tracks
12. **Full Stack Test Track** (`660e8400-e29b-41d4-a716-446655440012`) - 60 minutes
    - Comprehensive track covering urban, highway, and rural scenarios

13. **Proving Ground Circuit** (`660e8400-e29b-41d4-a716-446655440013`) - 90 minutes
    - Professional proving ground with all scenario types

---

## 🚀 How to Use

1. **Open the UI**: Navigate to http://localhost:3000
2. **Create a Simulation**: 
   - Select one or more scenarios from the list above
   - Select a suitable track
   - Configure your simulation parameters
   - Click "Run Simulation"

3. **Monitor the Flow**:
   - Watch the simulation progress in the UI
   - Check RabbitMQ at http://localhost:15672 to see events flowing
   - View webhook deliveries at http://localhost:8084/api/webhook-deliveries

## 💡 Recommended Combinations

### For Urban Testing:
- **Scenario**: Urban Traffic Navigation
- **Track**: Downtown City Circuit
- **Duration**: 15 minutes

### For Highway Testing:
- **Scenario**: Adaptive Cruise Control
- **Track**: Autobahn Test Section
- **Duration**: 30 minutes

### For Safety Testing:
- **Scenario**: Emergency Braking
- **Track**: Emergency Response Track
- **Duration**: 12 minutes

### For Weather Testing:
- **Scenario**: Rain Driving
- **Track**: Weather Simulation Track
- **Duration**: 22 minutes

### For Comprehensive Testing:
- **Scenario**: Multiple scenarios (Urban + Highway + Safety)
- **Track**: Full Stack Test Track
- **Duration**: 60 minutes

---

## 📊 Database Stats

- **Total Scenarios**: 16
  - Urban Driving: 4
  - Highway: 4
  - Safety: 4
  - Weather: 3
  - Edge Case: 1

- **Total Tracks**: 13
  - Comprehensive: 2
  - Weather: 2
  - Safety: 2
  - Highway: 2
  - Urban: 1
  - Suburban: 1
  - Mixed: 1
  - Mountain: 1
  - Parking: 1

---

## 🔄 End-to-End Flow

When you run a simulation from the UI:

1. **UI** → Sends simulation request to API Gateway
2. **API Gateway** → Routes to scenario-library-service
3. **Scenario Library Service** → Creates simulation, publishes event to RabbitMQ
4. **RabbitMQ** → Routes event to appropriate queues
5. **Webhook Service** → Consumes event, delivers to configured webhooks
6. **External Systems** → Receive webhook notifications

Happy Testing! 🎉
