using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class WhiteBloodCell : MonoBehaviour
{
    public float lifeTime = 10;
    public float currentLife = 0;
    public Microspace ms;
    public float speed;
    private Vector3 desiredPos;
    public float moveDir = 5;
    private float yPos;

    public bool helperT = false;
    public Material mHelper;
    private bool canMove = true;

    private Vector3 minBoundaries;
    private Vector3 maxBoundaries;

    void Start()
    {
        yPos = transform.position.y;
        desiredPos = new Vector3(transform.position.x + Random.Range(-moveDir, moveDir), yPos, transform.position.z + Random.Range(-moveDir, moveDir));
        Vector3 parentSize = transform.parent.GetComponent<Renderer>().bounds.size;
        minBoundaries = transform.parent.position - parentSize / 2f;
        maxBoundaries = transform.parent.position + parentSize / 2f;
    }

    // Update is called once per frame
    void Update()
    {
        currentLife += Time.deltaTime;
        if (canMove)
        {
            transform.position = Vector3.Lerp(transform.position, desiredPos, speed * Time.deltaTime);

            if (Vector3.Distance(transform.position, desiredPos) > 0.1f) return;

            float offsetX = Random.Range(-moveDir, moveDir);
            float offsetZ = Random.Range(-moveDir, moveDir);

            // Calculate the new desired position with the offsets and ensure it stays within the boundaries
            float newX = Mathf.Clamp(transform.position.x + offsetX, minBoundaries.x, maxBoundaries.x);
            float newZ = Mathf.Clamp(transform.position.z + offsetZ, minBoundaries.z, maxBoundaries.z);

            desiredPos = new Vector3(newX, yPos, newZ); transform.LookAt(desiredPos);
            transform.eulerAngles = new Vector3(transform.rotation.x, 180f, transform.rotation.z);
        }


        if (currentLife > lifeTime)
        {
            Destroy(gameObject);
        }
    }


    void OnTriggerEnter(Collider col)
    {
        if (col.gameObject.tag == "BacteriaSystem")
        {
            desiredPos = col.gameObject.transform.position; //give all inside bacteria this tag
        }
    }

    private void OnCollisionEnter(Collision col)
    {
        if (col.gameObject.tag == "BacteriaSystem")
        {
            ms.allBacteria.Remove(col.gameObject.GetComponent<BacteriaField_RLAgent02>());
            canMove = false;
            col.gameObject.GetComponent<BacteriaField_RLAgent02>().enabled = false;
            StartCoroutine(killBacteria(col.gameObject));
        }

        if(col.gameObject.tag == "HelperT" && !helperT)
        {
            gameObject.GetComponent<MeshRenderer>().material = mHelper;
            helperT = true;
            lifeTime += 10;
            speed += 0.75f;
            moveDir += 0.2f;
        }
    }

    IEnumerator killBacteria(GameObject bacteria)
    {
        yield return new WaitForSeconds(5);
        Destroy(bacteria);
        canMove = true;
    }
}
